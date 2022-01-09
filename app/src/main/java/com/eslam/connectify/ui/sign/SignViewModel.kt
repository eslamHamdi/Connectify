package com.eslam.connectify.ui.sign

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.connectify.ConnectifyApp
import com.eslam.connectify.domain.models.Response
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
class SignViewModel @Inject constructor(private val authUseCases:SignInUseCase,app: Application):
    AndroidViewModel(app) {



    private val _signInStatus:MutableState<Response<Boolean>> = mutableStateOf(Response.Success(false))

    val signInState:State<Response<Boolean>>
    get() = _signInStatus

    private val _signInIntent:MutableState<Intent?> = mutableStateOf(null)

    val signInIntent:State<Intent?>
        get() = _signInIntent


    init {
        silentSignIn()
    }





    fun onLoginResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse


        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            _signInStatus.value = Response.Success(true)
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            val userPressedBackButton = (response == null)
            if (userPressedBackButton) {
                // _authResultCode.value = AuthResultCode.CANCELLED
                _signInStatus.value = Response.Error("login Cancelled!!")
                Log.d(null, "Login cancelled by user")
                return
            }
            val error = response?.error?.message
            when (response?.error?.errorCode) {


                ErrorCodes.NO_NETWORK -> {
                    //_authResultCode.value = AuthResultCode.NO_NETWORK
                    _signInStatus.value = Response.Error(error!!)
                    Log.d(null, "Login failed on network connectivity")
                }

                ErrorCodes.PROVIDER_ERROR-> {_signInStatus.value = Response.Error(error!!)}
                ErrorCodes.EMAIL_MISMATCH_ERROR    -> {_signInStatus.value = Response.Error(error!!)}
                ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR ->{_signInStatus.value = Response.Error(error!!)}

                else -> {
                    _signInStatus.value = Response.Error(error!!)
                    Log.d(null, "Login failed")

                }

            }
        }
    }


    fun silentSignIn()
    {
        viewModelScope.launch {
            authUseCases.silentSignIn(getApplication()).collect { task->

                if (task != null && task.result?.user != null) {

                    _signInStatus.value = Response.Success(true)

                }else{
                    _signInStatus.value = Response.Success(false)
                }

            }
        }

    }

    fun Signup(){

        val user = Firebase.auth.currentUser

        if (user != null) {
            // User is signed in
            _signInStatus.value = Response.Success(true)

        } else {
            // No user is signed in
                viewModelScope.launch {
                    authUseCases.launchSignInIntent().collect {

                        _signInIntent.value = it

                    }
                }

        }

    }


//    @ExperimentalCoroutinesApi
//    fun getFirebaseAuthState() = callbackFlow  {
//        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
//            trySend(auth.currentUser == null)
//        }
//        Firebase.auth.addAuthStateListener(authStateListener)
//        awaitClose {
//            Firebase.auth.removeAuthStateListener(authStateListener)
//        }
//    }
}