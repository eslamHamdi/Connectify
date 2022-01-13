package com.eslam.connectify.data.repositories

import android.net.Uri
import com.eslam.connectify.domain.datasources.ProfileRepository
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.models.User
import com.google.firebase.auth.FirebaseAuth
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
//            currentUser?.updateProfile( UserProfileChangeRequest.Builder().setDisplayName(name).build()
//
//            )
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


            database.reference.child("users").child(currentUser?.uid!!).setValue(user).addOnCompleteListener {
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

            database.reference.child("users").child(userId!!).also {
                it.child("name").setValue(name)
                it.child("profileImage").setValue(img.toString())

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
            database.reference.child("users").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   val name= snapshot.child(auth.currentUser?.uid!!).child("name").value  as String?
                    val img =snapshot.child(auth.currentUser?.uid!!).child("profileImage").value  as String?
                    val email = snapshot.child(auth.currentUser?.uid!!).child("email").value  as String?
                    val phone = snapshot.child(auth.currentUser?.uid!!).child("phone").value  as String?
                    val contacts = snapshot.child(auth.currentUser?.uid!!).child("contacts").value
                    user = User(auth.currentUser!!.uid, name, email,phone,img,
                        contacts as MutableList<User>?
                    )


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


}