package com.eslam.connectify.data.repositories

import android.util.Log
import com.eslam.connectify.data.utils.singleValueEvent
import com.eslam.connectify.data.utils.valueEventFlow
import com.eslam.connectify.domain.datasources.ChatRepository
import com.eslam.connectify.domain.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val auth: FirebaseAuth, private val database: FirebaseDatabase):ChatRepository {
    override  suspend fun findContactByPhone(phone: String): Flow<Response<List<ChatRoom?>>> {
        return callbackFlow {

            trySend(Response.Loading)
            val coroutineContext =  this.coroutineContext


            val scope = CoroutineScope(coroutineContext)
            var users:List<User?>? = null
            var rooms:List<ChatRoom?> = listOf()
            var errorMsg:String? = null
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        users = snapshot.children.map {

                            it.getValue(User::class.java)
                        }

                        if (users != null)
                        {
                            scope.launch {
                                rooms = getUsersAsRooms(users)

                                trySend(Response.Success(rooms))
                            }


                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                        errorMsg = error.message

                        trySend(Response.Error(errorMsg!!))

                    }

                }


            val dataBasesRef = database.reference.child("users")
            dataBasesRef.orderByChild("phone").equalTo(phone)

            awaitClose { dataBasesRef.removeEventListener(listener)
               coroutineContext.cancel()
           }
       }
    }

    override suspend fun listenToRoomsAdded(): Flow<Response<List<ChatRoom?>>> {

        val rooms:MutableList<ChatRoom> = mutableListOf()
     return  database.reference.child("users").child(auth.currentUser?.uid!!).child("contacts")
           .valueEventFlow().transform<Response<DataSnapshot>,Response<List<ChatRoom?>>> { value: Response<DataSnapshot> ->

               if (value is Response.Success)
               {

                   val roomsIds =value.data.value as Map<*,*>?
                   //Log.e("RoomsUpdates", "listenToRoomsAdded:$roomsIds", )
                   roomsIds?.forEach {
                       val roomId = it.value as String?
                       val room = database.reference.child("Rooms").child(auth.currentUser?.uid!!).child(roomId!!).get().await().getValue(ChatRoom::class.java)
                       val participant1 = room?.id?.substringBefore("_")
                       val participant2 = room?.id?.substringAfter("_")
                       val currentUser = auth.currentUser!!.uid


                       if (room != null) {

                           if (participant1 != currentUser)
                           {
                               room.name = database.reference.child("users").child(participant1!!).child("name").get().await().getValue(String::class.java)
                           }else
                           {
                               room.name = database.reference.child("users").child(participant2!!).child("name").get().await().getValue(String::class.java)
                           }
                           rooms.add(room)

                       }
                   }
                  emit(Response.Success(rooms))
                   Log.e("RoomsUpdates", "listenToRoomsAdded:$rooms", )
               }else if (value is Response.Error)
               {
                   emit(Response.Error("failed to get rooms updates"))
               }else if (value is Response.Loading)
               {
                   emit(Response.Loading)
               }

         }.catch {
             Log.d("RoomUpdates", "listenToRoomsAdded: failed ")
         }



    }

    override suspend fun listenToRoomChanges(): Flow<Response<ChatRoom>?> {
        return callbackFlow {

            val roomsRef =database.reference.child("Rooms").child(auth.currentUser?.uid!!)

            val childEventListener:ChildEventListener = object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                    trySend(Response.Success(snapshot.getValue(ChatRoom::class.java)!!))
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }

            roomsRef.addChildEventListener(childEventListener)

            awaitClose {
                roomsRef.removeEventListener(childEventListener)
            }


        }
    }




    override  suspend fun getRoomsDemo(): Flow<Response<List<ChatRoom?>>> {
        //Response.Error(e.message!!)
        return flow {
            emit(Response.Loading)
            val rooms:MutableList<ChatRoom> = mutableListOf()
            val users:MutableList<User> = mutableListOf()
            var notContact:List<User>? = null
            val remainingRooms:MutableList<ChatRoom> = mutableListOf()

            val contactsSnap = database.reference.child("users").
            child(auth.currentUser?.uid!!).child("contacts").singleValueEvent()


            val usersSnap = database.reference.child("users").singleValueEvent()

            if (contactsSnap is Response.Success && contactsSnap.data.value != null)
            {
                Log.e("ContactsOnly", "getRoomsDemoCont:  ${contactsSnap.data}", )
               val list = contactsSnap.data.value as Map<*, *>
                if (list.isNotEmpty())
                {
                    list.forEach {
                            //val user = it.value
                            val contact: String = it.value as String
                            //Log.e(null, "getRoomsDemo0: $contact ", )
                            if (contact.isNotBlank()) {

                                val roomSnap = database.reference.child("Rooms").child(auth.currentUser?.uid!!).child(contact!!).get().await()
                                var room = roomSnap.getValue(ChatRoom::class.java)
                                room?.lastMessage = roomSnap.child("lastMessage").getValue(ChatMessage::class.java)
                                val participant1 = room?.id?.substringBefore("_")
                                val participant2 = room?.id?.substringAfter("_")
                                val currentUser = auth.currentUser!!.uid


                                if (room != null) {

                                    if (participant1 != currentUser)
                                    {
                                        room.name = database.reference.child("users").child(participant1!!).child("name").get().await().getValue(String::class.java)
                                    }else
                                    {
                                        room.name = database.reference.child("users").child(participant2!!).child("name").get().await().getValue(String::class.java)
                                    }
                                    rooms.add(room)
                                }
                            }
                        }

                }

            }


            if (usersSnap is Response.Success) {
                val list = usersSnap.data.children.toList()

                Log.e("AllUsers", "getRoomsDemo: $list ",)

                list?.forEach {

                    // Log.e(null, "getRoomsDemo: $it",)
                    //val user = it.getValue(User::class.java)
                    val id: String = it.child("uid").getValue(String::class.java)!!
                    val name: String = it.child("name").getValue(String::class.java)!!
                    //Log.e(null, "getRoomsDemoNameTest: $name",)
                    val phone: String = it.child("phone").getValue(String::class.java)!!
                    val img: String? = it.child("profileImage").getValue(String::class.java)
                    val contacts = it.child("contacts").value as Map<*, *>?
//                    val contactList = it
//                    user?.contacts = contactList
                    val user = if (contacts?.isNotEmpty() == true) User(
                        id,
                        name,
                        phone,
                        img,
                        contacts
                    ) else User(id, name, phone, img)
                    users.add(user)
                    //  Log.e(null, "getRoomsDemo2: $user",)
                }

            }
                    if (rooms.isNotEmpty())
                    {


                        Log.e("rooms of contacts", "getRoomsDemolist: ${rooms.toList()}", )
                        rooms.forEach { chatRoom ->
                            //  Log.e(null, "getRoomsDemo3: $it",)

                            notContact = users.filter { user ->
                                user.uid != chatRoom.id?.substringAfter("_") && user.uid != chatRoom.id?.substringBefore(
                                    "_"
                                )
                            }

                        }

                        if (notContact != null && notContact!!.isNotEmpty())
                        {
                            notContact?.forEach { user->
                                if ( user.uid != auth.currentUser?.uid)
                                {
                                    Log.e("filterContacts", "getRoomsDemo: $user", )

                                    //user?.uid+auth.currentUser?.uid
                                    val room = ChatRoom(user?.uid+"_"+auth.currentUser?.uid,
                                        ChatMessage(),user?.profileImage,user?.name, )

                                    remainingRooms.add(room)
                                }
                            }






                            if (remainingRooms.isNotEmpty()) {
                                rooms.addAll(remainingRooms)
                                Log.e("filteredContacts", "getRoomsDemo: $remainingRooms ", )
                            }
                        }

                    }else
                    {
                        users.forEach { user ->
                            if ( user?.uid != auth.currentUser?.uid)
                            {

                                // Log.e(null, "getRoomsDemo: rooms is empty +  $user", )
                                //user?.uid+auth.currentUser?.uid
                                val room = ChatRoom(
                                    user.uid +"_"+auth.currentUser?.uid,
                                    ChatMessage(), user.profileImage, user.name, )
                                rooms.add(room)
                            }
                        }

                    }
                   // Log.e(null, "getRoomsDemolist0: ${rooms.toList()}", )

            if (rooms.isNotEmpty())
            {
                emit(Response.Success(rooms.toList()))

            }

        }.catch {
            //Response.Error("failed to get rooms")
            Log.e("TAG", "getRoomsDemo: Error getting rooms", )
        }


        }

    override suspend fun setUserState(state: String) {
        try {
            database.reference.child("usersStates").child(auth.currentUser?.uid!!).setValue(state).await()
        }catch (e:Exception)
        {
            Log.e(null, "setUserState: Failed", )
        }

    }


    override fun signOut(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    suspend fun getUsersAsRooms(users: List<User?>?): List<ChatRoom?> {
        var rooms: MutableList<ChatRoom?>? = mutableListOf()
        if (!users.isNullOrEmpty()) {
            users.forEach {

                it?.let {
                    val id = auth.currentUser?.uid + it.uid
                    val response = database.reference.child("Rooms").child(id).singleValueEvent()
                    if (response is Response.Success) {
                        var room = response.data.getValue(ChatRoom::class.java)

                        rooms?.add(room)


                    } else {

                        val room = ChatRoom(id, ChatMessage(), it.profileImage, it.name)

                        rooms?.add(room)

                    }


                }
            }
        }


        return rooms?.toList() ?: listOf()
    }


}