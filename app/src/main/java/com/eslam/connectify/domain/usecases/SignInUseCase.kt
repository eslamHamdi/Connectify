package com.eslam.connectify.domain.usecases

import android.content.Context
import android.content.Intent
import com.eslam.connectify.domain.AuthDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authSource:AuthDataSource) {



    fun launchSignInIntent(): Flow<Intent>
    {
      return authSource.buildLoginIntent()
    }


    fun silentSignIn(context: Context): Flow<Task<AuthResult>?>
    {
       return authSource.silentSignIn(context)
    }


    fun signOut()
    {

    }
}