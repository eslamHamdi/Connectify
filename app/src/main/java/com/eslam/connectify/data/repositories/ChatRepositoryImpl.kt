package com.eslam.connectify.data.repositories

import com.eslam.connectify.data.utils.singleValueEvent
import com.eslam.connectify.data.utils.valueEventFlow
import com.eslam.connectify.domain.datasources.ChatRepository
import com.eslam.connectify.domain.models.ChatMessage
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val auth: FirebaseAuth, private val database: FirebaseDatabase):ChatRepository {
    override  fun findContactByName(name: String): Flow<Response<List<ChatRoom?>>> {
       return callbackFlow {

           val context =this.coroutineContext
           trySend(Response.Loading)
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
                           //rooms = getRooms(users)
                          // trySend(Response.Success(getRooms(users)))
                       }


                   }

                   override fun onCancelled(error: DatabaseError) {

                       errorMsg = error.message

                       trySend(Response.Error(errorMsg!!))

                   }

               }


          val dataBasesRef = database.reference.child("users")
          dataBasesRef.orderByChild("name").equalTo(name)

           awaitClose { dataBasesRef.removeEventListener(listener) }
       }
    }

    override fun addContact(userId: String): Flow<Boolean> {

       return flow {
           database.reference.child("users").child(auth.currentUser?.uid!!)
               .child("contacts").push().setValue(userId).await()

           emit(true)

       }.catch {
           emit(false)
       }
    }

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
            val users:MutableList<User> = mutableListOf()



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
                                var response =database.reference.child("users").child(contact!!).singleValueEvent()
                                if (response is Response.Success)
                                {
                                    users.add(response.data.getValue(User::class.java)!!)

                                }


                            }
                        }

                    }
                    getRooms(users)
                }



        return flow
    }

    override fun signOut(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

   suspend fun getRooms(users:List<User?>?): List<ChatRoom?>
    {
        var rooms:MutableList<ChatRoom?>? = mutableListOf()
        if (!users.isNullOrEmpty())
        {
            users.forEach {

                it?.let {
                    val id = auth.currentUser?.uid + it.uid
                    val response = database.reference.child("Rooms").child(id).singleValueEvent()
                             if (response is Response.Success)
                             {
                                var room = response.data.getValue(ChatRoom::class.java)

                                 rooms?.add(room)



                                 }else{

                                     val room = ChatRoom(id, ChatMessage(),it.profileImage,it.name)

                                     rooms?.add(room)

                             }



                }
            }
        }


        return rooms?.toList() ?: listOf()
    }


}