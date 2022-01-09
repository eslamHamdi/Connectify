package com.eslam.connectify.domain

import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthDataSource {


    fun buildLoginIntent(): Flow<Intent>
    fun buildLoginActivityResult(): FirebaseAuthUIActivityResultContract =
        FirebaseAuthUIActivityResultContract()

    //fun onLoginResult(result: FirebaseAuthUIAuthenticationResult)

    fun silentSignIn(context: Context): Flow<Task<AuthResult>?>
}