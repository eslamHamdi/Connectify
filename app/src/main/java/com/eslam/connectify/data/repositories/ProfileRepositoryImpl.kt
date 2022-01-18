package com.eslam.connectify.data.repositories

import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val auth: FirebaseAuth,private val database: FirebaseDatabase,
private val storage:FirebaseStorage):ProfileRepository{

    init {
        database.setPersistenceEnabled(true)
    }
    override fun createProfile(img: Uri?, name: String): Flow<Response<Any>> {
        return flow {

            emit(Response.Loading)

            var msg:String? = ""
            val currentUser = auth.currentUser


            if (name.isEmpty() || name.isBlank())
            {
                emit(Response.Error("Please Enter Profile Name"))
            }
            val user = User(currentUser?.uid,name, currentUser?.email, currentUser?.phoneNumber)
            if (img != null)
            {
                val storageReference = storage.reference.child("Profiles").child(currentUser?.uid!!)

                storageReference.putFile(img).addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        storageReference.downloadUrl.addOnCompleteListener { task->
                            user.profileImage = task.result.toString()
                        }
                    }else{
                        msg = it.exception.toString()
                    }
                }
            }

            val childPath = currentUser!!.uid


            database.reference.child("users").child(childPath!!).setValue(user).addOnCompleteListener {
                msg = if (it.isSuccessful) {
                    ""
                }else{
                    it.exception?.message ?:"Failed To Create Account Check Your Internet and Try again "

                }


            }

            if (!msg.isNullOrBlank())
            {
                emit(Response.Error(msg!!))
            }else{

                emit(Response.Success("Profile Created"))
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

               if (name != null) {it.child("name").setValue(name)}
                if (photoUrl != null){it.child("profileImage").setValue(photoUrl.toString())}

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
            database.reference.child("users").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   user = snapshot.child(userId!!).getValue(User::class.java)



                }

                override fun onCancelled(error: DatabaseError) {

                    exception = error.message
                }



            })

            if (exception != null)
            {
                emit(Response.Error(exception!!))
            }else
            {
                emit(Response.Success(user))
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


    fun uploadPhoto(img:Uri?,user:FirebaseUser?) :String?
    {
        var downloadUrl:String? = null
        if (img != null && user != null)
        {
            val storageReference = storage.reference.child("Profiles").child(user.uid)

            storageReference.putFile(img).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    storageReference.downloadUrl.addOnCompleteListener { task->
                        downloadUrl = task.result.toString()
                    }
                }else{
                    Log.e(null, "uploadPhoto: ${it.exception?.message}" )
                }
            }
        }

        return downloadUrl
    }


//    fun editMail(mail:String?):String?{
//        return mail?.replace('.','-')
//    }

}