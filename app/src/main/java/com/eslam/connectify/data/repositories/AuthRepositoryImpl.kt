package com.eslam.connectify.data.repositories

import android.content.Context
import android.content.Intent
import android.util.Log
import com.eslam.connectify.R
import com.eslam.connectify.domain.datasources.AuthDataSource
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl: AuthDataSource {

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
    )

    override fun buildLoginIntent(): Flow<Intent> {

// Create and launch sign-in intent
        val layout = AuthMethodPickerLayout
            .Builder(R.layout.signin_layout)
            .setGoogleButtonId(R.id.googleBtn)
            .setEmailButtonId(R.id.email_button)
            .setPhoneButtonId(R.id.phone_button)
            .build()


        return flow {

            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
               .setLogo(R.drawable.ic_connectify)
                .setTheme(R.style.Theme_Connectify_NoActionBar)
                //.setAuthMethodPickerLayout(layout)
                .setIsSmartLockEnabled(true)
                .build()

            emit(intent)
        }
    }

    override fun getUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

//    override fun silentSignIn(context: Context): Flow<Task<AuthResult>?> {
//        return flow {
//            var task:Task<AuthResult>? = null
//
//           task = AuthUI.getInstance().silentSignIn(context, providers).continueWithTask {
//               if (task?.isSuccessful!!) {
//                   task;
//               } else {
//                   // Ignore any exceptions since we don't care about credential fetch errors.
//                   null
//               }
//
//
//           }.addOnCompleteListener {
//               task = if (it.isSuccessful) {
//
//                   it
//               } else {
//                   Log.e(null, "silentSignIn: failed!! ", )
//                   null
//               }
//           }
//
//            emit(task)
//        }.catch {
//            Log.e(null, "silentSignIn: Failed", )
//        }
//
//
//
//
//        }
//





    }

