package com.eslam.connectify.domain.usecases

import android.content.Intent
import com.eslam.connectify.domain.datasources.AuthDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authSource: AuthDataSource) {



    fun launchSignInIntent(): Flow<Any>
    {
      return authSource.buildLoginIntent()
    }





    fun getAuthUser() = authSource.getUser()
}