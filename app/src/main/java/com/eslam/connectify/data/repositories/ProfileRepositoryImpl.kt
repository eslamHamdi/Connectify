package com.eslam.connectify.data.repositories

import android.net.Uri
import android.util.Log
import com.eslam.connectify.data.utils.singleValueEvent
import com.eslam.connectify.domain.datasources.ProfileRepository
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val auth: FirebaseAuth,private val database: FirebaseDatabase,
private val storage:FirebaseStorage):ProfileRepository{

    init {
        database.setPersistenceEnabled(true)
    }
    override suspend fun createProfile(img: Uri?, name: String): Response<Any> {
        var msg:String? = ""
        val currentUser = auth.currentUser
        val childPath = currentUser!!.uid


        if (name.isEmpty() || name.isBlank())
        {
            return (Response.Error("Please Enter Profile Name"))
        }
        Log.e(null, "createProfile: $name", )
        var user = User(currentUser.uid,name, currentUser.phoneNumber, contacts = listOf())
        if (img != null)
        {
            val storageReference = storage.reference.child("Profiles").child(currentUser.uid)

            val upLoadTask = storageReference.putFile(img).await()


            if (upLoadTask.task.isSuccessful)
            {
                storageReference.downloadUrl.await()?.let {
                    Log.e(null, "createProfile: ${it.toString()}",)
                    user.profileImage = it.toString()
                    Log.e(null, "user: $user",)
                }
                try {
//                    val userMap:MutableMap<String,User> = mutableMapOf()
//                    userMap.put()


                    Log.e(null, "createProfile: entered try", )

               val userDataList:MutableList<User> = mutableListOf()
                    userDataList.add(user)
                    database.reference.child("users").child(childPath).setValue(user).await()


                        Log.e(null, "createProfile: done ", )

                    return Response.Success("Profile Created")
                }catch (e:Exception) {
                    Log.e(null, "createProfile: ${e.message}", )
                    return Response.Error(e.message!!)

                }



            }else{
                msg = upLoadTask.task.exception.toString()
                return Response.Error(msg)
            }

        }else{

            Log.e(null, "createProfile: $user ", )

            return try {
                database.reference.child("users").child(childPath).setValue(user).await()
                (Response.Success("Profile Created"))
                // Log.e(null, "createProfile: done ", )
            }catch (e:Exception) {
                msg = e.message
                Log.e("Profile", "createProfile: ${e.message} ", )
                (Response.Error(msg!!))


            }


        }


    }











    override fun updateProfile(img: Uri?, name: String?): Flow<Response<Any>> {

        return flow {

            emit(Response.Loading)
            val userId = auth.currentUser?.uid
            val photoUrl = uploadPhoto(img,auth.currentUser)

            //val obj:HashMap<String,Any>

            database.reference.child("users").child(userId!!).also {

               if (name != null) {it.child("name").setValue(name).await()}
                if (photoUrl != null){it.child("profileImage").setValue(photoUrl.toString()).await()}

                emit(Response.Success("Profile Updated"))
            }


                }.catch {
                    emit(Response.Error("Updating Profile Failed"))
        }


    }

    override fun getUserInfo(): Flow<Response<User?>> {
        return flow {
            emit(Response.Loading)
            var user:User? = null
            var exception:String? = null
            val userId = auth.currentUser?.uid
            val response = database.reference.child("users").singleValueEvent()

            if (response is Response.Success)
            {
                user = response.data.child(userId!!).getValue(User::class.java)
                emit(Response.Success(user))
            }else if (response is Response.Error)
            {
                    exception = response.message
                emit(Response.Error(exception))
                }

        }.catch {
            emit(Response.Error("Failed to get user info!!"))
        }
    }

    override fun getUserAuthId(): String? {
        return auth.currentUser?.uid
    }

    override fun getSignInState(): FirebaseUser? {
        return auth.currentUser
    }


   private suspend fun uploadPhoto(img:Uri?, user:FirebaseUser?) :String?
    {
        var downloadUrl:String? = null
        if (img != null && user != null)
        {
            val storageReference = storage.reference.child("Profiles").child(user.uid)

            val uploadTask =storageReference.putFile(img).await().task
                if (uploadTask.isSuccessful)
                {
                    downloadUrl = storageReference.downloadUrl.await().toString()

                }else{
                    Log.e(null, "uploadPhoto: ${uploadTask.result.error}" )
                }

        }

        return downloadUrl
    }




}