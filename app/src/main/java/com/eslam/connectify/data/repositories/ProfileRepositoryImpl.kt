package com.eslam.connectify.data.repositories

import android.net.Uri
import com.eslam.connectify.domain.datasources.ProfileRepository
import com.eslam.connectify.domain.models.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val auth: FirebaseAuth,private val database: FirebaseDatabase,
private val storage:FirebaseStorage):ProfileRepository{
    override fun createProfile(img: Uri?, name: String): Flow<Response<Boolean>> {
        return flow {

        }
    }


}