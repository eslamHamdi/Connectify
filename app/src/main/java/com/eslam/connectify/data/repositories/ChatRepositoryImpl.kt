package com.eslam.connectify.data.repositories

import android.util.Log
import com.eslam.connectify.data.utils.singleValueEvent
import com.eslam.connectify.data.utils.valueEventFlow
import com.eslam.connectify.domain.datasources.ChatRepository
import com.eslam.connectify.domain.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
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

    override suspend fun listenToRoomChanges(): Flow<Response<List<ChatRoom?>>> {

        val rooms:MutableList<ChatRoom> = mutableListOf()
     return  database.reference.child("users").child(auth.currentUser?.uid!!).child("contacts")
           .valueEventFlow().transform<Response<DataSnapshot>,Response<List<ChatRoom?>>> { value: Response<DataSnapshot> ->

               if (value is Response.Success)
               {
                   val roomsIds =value.data.value as Map<*,*>?
                   roomsIds?.forEach {
                       val roomId = it.value as String?
                       val room = database.reference.child("Rooms").child(roomId!!).get().await().getValue(ChatRoom::class.java)
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
               }else if (value is Response.Error)
               {
                   emit(Response.Error("failed to get rooms updates"))
               }else if (value is Response.Loading)
               {
                   emit(Response.Loading)
               }

         }.catch {
             Log.d(null, "listenToRoomChanges: failed ")
         }.flowOn(Dispatchers.IO)



    }

//    override fun addContact(userId: String): Flow<Boolean> {
//
//       return flow {
//           database.reference.child("users").child(auth.currentUser?.uid!!)
//               .child("contacts").push().setValue(userId).await()
//
//           emit(true)
//
//       }.catch {
//           emit(false)
//       }
//    }

    override fun addRoom(room: ChatRoom,contactId:String): Flow<Boolean> {

        return flow {
            val id = auth.currentUser?.uid  + contactId
            database.reference.child("Rooms").child(id).setValue(room).await()
            emit(true)
        }.catch {
            emit(false)
        }
    }

    override suspend fun getRooms(): Flow<List<ChatRoom?>> {

            var contacts:MutableList<String?> = mutableListOf()
            val users:MutableList<ChatRoom> = mutableListOf()



           val flow = database.reference.child("users").child(auth.currentUser!!.uid).child("contacts").valueEventFlow()
                .map {
                    if (it is Response.Success)
                    {
                       contacts= it.data.children.map { data->
                            data.getValue(String::class.java)
                        }.toMutableList()
                        if (contacts.isNotEmpty())
                        {
                            contacts.forEach { contact->
                                var response =database.reference.child("Rooms").child(contact!!).singleValueEvent()
                                if (response is Response.Success)
                                {
                                    users.add(response.data.getValue(ChatRoom::class.java)!!)

                                }


                            }
                        }

                    }
                    users

                }.catch {
                   Log.e(null, "getRooms: Failed to fetch saved rooms ", )
               }



        return flow
    }

    override  suspend fun getRoomsDemo(): Flow<Response<List<ChatRoom?>>> {
        //Response.Error(e.message!!)
        return flow {
            emit(Response.Loading)
            val rooms:MutableList<ChatRoom> = mutableListOf()
            val usersId:MutableList<User> = mutableListOf()

            val contactsSnap = database.reference.child("users").
            child(auth.currentUser?.uid!!).child("contacts").singleValueEvent()


            val usersSnap = database.reference.child("users").singleValueEvent()

            if (contactsSnap is Response.Success && contactsSnap.data.value != null)
            {
                Log.e(null, "getRoomsDemoCont:  ${contactsSnap.data}", )
               val list = contactsSnap.data.value as Map<*, *>
                    list?.let { map->
                        map.forEach {
                            //val user = it.value
                            val contact: String = it.value as String
                            Log.e(null, "getRoomsDemo0: $contact ", )
                            if (contact.isNotBlank()) {

                                val roomSnap = database.reference.child("Rooms").child(contact!!).get().await()
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


            if (usersSnap is Response.Success)
            {
               val list = usersSnap.data.children
                list.forEach {

                    Log.e(null, "getRoomsDemo: $it",)
                    //val user = it.getValue(User::class.java)
                    val id : String = it.child("uid").getValue(String::class.java)!!
                    val name : String = it.child("name").getValue(String::class.java)!!
                    Log.e(null, "getRoomsDemoNameTest: $name",)
                    val phone : String = it.child("phone").getValue(String::class.java)!!
                    val img : String = it.child("profileImage").getValue(String::class.java)!!
                    val contacts = it.child("contacts").value as Map<*, *>?
//                    val contactList = it
//                    user?.contacts = contactList
                    val user = if(contacts.isNullOrEmpty())User(id,name,phone,img, contacts) else User(id,name,phone,img)
                    //usersId.add(user!!)
                    Log.e(null, "getRoomsDemo2: $user",)
                    if (rooms.isNotEmpty())
                    {
                        val roomList:MutableList<ChatRoom> = mutableListOf()
                        Log.e(null, "getRoomsDemolist1: ${rooms.toList()}", )
                        rooms.forEach { chatRoom->
                            Log.e(null, "getRoomsDemo3: $it",)

                            if (!(chatRoom.id?.contains(user?.uid!!))!!) {

                                if ( user?.uid != auth.currentUser?.uid)
                                {

                                    //user?.uid+auth.currentUser?.uid
                                    val room = ChatRoom(user?.uid+"_"+auth.currentUser?.uid,
                                        ChatMessage(),user?.profileImage,user?.name, )

                                    roomList.add(room)
                                }

                            }


                        }
                        if (roomList.isNotEmpty()) {
                            rooms.addAll(roomList)
                        }
                    }else
                    {
                        if ( user?.uid != auth.currentUser?.uid)
                        {

                            //user?.uid+auth.currentUser?.uid
                            val room = ChatRoom(user?.uid+"_"+auth.currentUser?.uid,
                                ChatMessage(),user?.profileImage,user?.name, )
                            rooms.add(room)
                        }
                    }

                }



//                if ( rooms.isNotEmpty())
//                {
//                    Log.e(null, "getRoomsDemo4: ", )
//                    rooms.forEach {
//                        val response = database.reference.child("${it.id}").child("lastMessage").singleValueEvent()
//                        if (response is Response.Success)
//                        {
//                            val lastMsg = response.data.getValue(ChatMessage::class.java)
//                            it.lastMessage = lastMsg
//                        }
//                    }
//
//                }
                emit(Response.Success(rooms.toList()))
                Log.e(null, "getRoomsDemolist1: ${rooms.toList()}", )
            }



            emit(Response.Success(rooms.toList()))
            Log.e(null, "getRoomsDemolist2: ${rooms.toList()}", )
        }.catch {
            //Response.Error("failed to get rooms")
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