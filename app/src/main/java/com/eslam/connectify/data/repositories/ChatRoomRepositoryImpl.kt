package com.eslam.connectify.data.repositories

import android.util.Log
import com.eslam.connectify.data.utils.singleValueEvent
import com.eslam.connectify.data.utils.valueEventFlow
import com.eslam.connectify.domain.datasources.ChatRoomRepository
import com.eslam.connectify.domain.models.ChatMessage
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRoomRepositoryImpl @Inject constructor(private val database: FirebaseDatabase,private val auth:FirebaseAuth
):ChatRoomRepository {
    override suspend fun sendMessage(msg: ChatMessage,room:ChatRoom) {

        try {
            val contactsResponse =database.reference.child("users").child(auth.currentUser?.uid!!).child("contacts").singleValueEvent()

            if (contactsResponse is Response.Success)
            {
                val ti = object : GenericTypeIndicator<HashMap<String?, String?>?>() {}
                val contactsMap  = contactsResponse.data.getValue(ti)

                val roomList = contactsMap?.values
                val senderId:String? = msg.senderId
                val receiverId : String? = if(room.id?.substringBefore("_") == auth.currentUser?.uid!!) room.id.substringAfter("_") else room.id?.substringBefore("_")
                var added = false
                roomList?.forEach {
                    if (it?.substringAfter("_") == receiverId as String || it?.substringBefore("_")== receiverId.toString())
                    {
                        added = true
                    }
                }
               room.lastMessage = msg
                val roomUpdate:MutableMap<String,Any> = mutableMapOf()
               roomUpdate[room.id!!] = room


                //database.reference.child("notifications").child(auth.currentUser!!.uid)

                if (roomList.isNullOrEmpty() || !added) {
                    database.reference.child("messages").child(room.id!!).push().setValue(msg).await()
                    database.reference.child("users").child(auth.currentUser?.uid!!).child("contacts").push().setValue(room.id).await()
                    database.reference.child("Rooms").child(auth.currentUser?.uid!!).child(room.id).setValue(room).await()
                    database.reference.child("Rooms").child(auth.currentUser?.uid!!).child(room.id).child("lastMessage").setValue(msg).await()
                    database.reference.child("Rooms").child(receiverId!! as String).child(room.id).setValue(room).await()
                   database.reference.child("Rooms").child(receiverId as String).child(room.id).child("lastMessage").setValue(msg).await()
                    database.reference.child("users").child(receiverId as String).child("contacts").push().setValue(room.id).await()

                }else {
                    //database.reference.child("Rooms").child(auth.currentUser?.uid!!).child(room.id!!).child("lastMessage").setValue(msg).await()
                    database.reference.child("Rooms").child(auth.currentUser?.uid!!).updateChildren(roomUpdate).await()
                    database.reference.child("Rooms").child(receiverId!! as String).updateChildren(roomUpdate).await()
                   // database.reference.child("Rooms").child(receiverId!!).child(room.id).child("lastMessage").setValue(msg).await()
                    database.reference.child("messages").child(room.id).push().setValue(msg).await()
                }
            }


        }catch (e:Exception)
        {
            Log.e(null, "sendMessage: ${e.message}", )
        }




    }

    override suspend fun getRoomMessages(roomId:String): Flow<Response<List<ChatMessage>?>?> {
        val messages:MutableList<ChatMessage> = mutableListOf()
        var response:Response<List<ChatMessage>?>? = null
        val flow = database.reference.child("messages").child(roomId).valueEventFlow().
        map {

            if (it is Response.Success)
            {
                var cnt = 0
                //Log.e(null, "getRoomMessages: ${++cnt} ", )

               it.data.children.forEach { child->
                    val message =child.getValue(ChatMessage::class.java)
                    if (message != null) {
                        messages.add(message)

                    }

                }
                //Log.e(null, "getRoomMessages2: ${messages} ", )
                response = Response.Success(messages.toList())

            }else if (it is Response.Error)
            {
                response = Response.Error(it.message)
            }else{
                response = Response.Loading
            }
            response

        }.catch {
            Log.e(null, "getRoomMessages: Failed", )
        }.flowOn(Dispatchers.IO)



        return flow

    }

    override fun getSenderId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun sendUserActivityState(status: String,roomId: String) {

        database.reference.child("chatStates").child(roomId).child(auth.currentUser?.uid!!).setValue(status)
    }

    override fun getContactStatus(roomId:String): Flow<String> {
        return callbackFlow {


            val contactId = if (roomId.substringAfter("_") == auth.currentUser?.uid!!) roomId.substringBefore("_") else roomId.substringAfter("_")
           val ref =  database.reference.child("usersStates").child(contactId)


            val stateListener = object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.toList()
                    Log.e("State", "onDataChange: $snapshot ", )
                    trySend(snapshot.getValue(String::class.java)!!)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }

            ref.addValueEventListener(stateListener)

            awaitClose {
                ref.removeEventListener(stateListener)
            }


        }
    }

    override suspend fun getContactTypingState(roomId:String): Flow<Response<Boolean>> {
        return callbackFlow {
            val contactId:String = if (roomId.substringAfter("_")==auth.currentUser?.uid!!) roomId.substringBefore("_") else
                roomId.substringAfter("_")
            val ref =database.reference.child("chatStates").child(roomId).child(contactId)

            val valueListener = object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val state = snapshot.getValue(String::class.java)
                    if (state == "Typing") {
                        trySend(Response.Success(true))
                    }else {
                        trySend(Response.Success(false))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }

            ref.addValueEventListener(valueListener)

            awaitClose {
                ref.removeEventListener(valueListener)
            }

        }
    }


}