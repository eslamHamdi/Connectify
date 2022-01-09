package com.eslam.connectify.data.repositories

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.util.Log
import com.eslam.connectify.R
import com.eslam.connectify.domain.AuthDataSource
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository:AuthDataSource {

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
    )

    override fun buildLoginIntent(): Flow<Intent> {

// Create and launch sign-in intent


        return flow {

            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_connectify)
                .setTheme(R.style.Theme_Connectify_NoActionBar)
                .setIsSmartLockEnabled(true)
                .build()

            emit(intent)
        }
    }

    override fun silentSignIn(context: Context): Flow<Task<AuthResult>?> {
        return flow {
            var task:Task<AuthResult>? = null

            AuthUI.getInstance().silentSignIn(context, providers).addOnCompleteListener {
                task = if (it.isSuccessful) {
                    it
                } else {
                    // Ignore any exceptions since we don't care about credential fetch errors.
                    null
                }
            }

            emit(task)
        }




        }






    }


