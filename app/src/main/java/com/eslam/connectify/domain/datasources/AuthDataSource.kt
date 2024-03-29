package com.eslam.connectify.domain.datasources

import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthDataSource {


    fun buildLoginIntent(): Flow<Intent>

    fun getUser():FirebaseUser?

    //fun silentSignIn(context: Context): Flow<Task<AuthResult>?>
}