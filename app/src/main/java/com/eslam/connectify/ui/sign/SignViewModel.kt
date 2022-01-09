package com.eslam.connectify.ui.sign

import android.app.Activity
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.connectify.ConnectifyApp
import com.eslam.connectify.domain.usecases.SignInUseCase
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(private val authUseCases:SignInUseCase,app: ConnectifyApp):
    AndroidViewModel(app) {





    init {



    }



    fun onLoginResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse


        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            val userPressedBackButton = (response == null)
            if (userPressedBackButton) {
                // _authResultCode.value = AuthResultCode.CANCELLED
                Log.d(null, "Login cancelled by user")
                return
            }
            when (response?.error?.errorCode) {
                ErrorCodes.NO_NETWORK -> {
                    //_authResultCode.value = AuthResultCode.NO_NETWORK

                    Log.d(null, "Login failed on network connectivity")
                }

                ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR -> {}
                else -> {
                    Log.d(null, "Login failed")
                    //_authResultCode.value = AuthResultCode.ERROR
                }

            }
        }
    }


    fun silentSignIn()
    {
        viewModelScope.launch {
            authUseCases.silentSignIn(getApplication()).collect { task->

                if (task != null && task.result?.user != null) {

                }else{

                }

            }
        }

    }

    fun Signup(){

        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in

        } else {
            // No user is signed in
                viewModelScope.launch {
                    authUseCases.launchSignInIntent().collect {

                    }
                }

        }

    }
}