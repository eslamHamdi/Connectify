package com.eslam.connectify.ui.sign

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel

import com.eslam.connectify.domain.models.Response
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

@Composable
fun LoginScreen(scaffoldState: ScaffoldState,navigate:()->Unit)
{
        val viewModel:SignViewModel = hiltViewModel<SignViewModel>()
    val context = LocalContext.current
        val intentState = remember{viewModel.signInIntent}
        val signInState = remember{viewModel.signInState}
        val loginLauncher = rememberLauncherForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result ->
            if (result != null) {
                viewModel.onLoginResult(result)
            }
        }

        when (signInState.value)
        {
            is Response.Success->
            {
                if (!(signInState.value as Response.Success<Boolean>).data)
                {
                    LaunchedEffect(key1 = true){
                        viewModel.Signup()
                    }
                }else
                {

                    navigate.invoke()
                }

            }

            is Response.Error ->{
                LaunchedEffect(key1 = Unit){
                    //scaffoldState.snackbarHostState.showSnackbar((signInState.value as Response.Error).message)
                    Toast.makeText(context,(signInState.value as Response.Error).message,Toast.LENGTH_SHORT).show()
                }
            }

            else ->{

            }


        }

        LaunchedEffect(key1 = intentState  ){

            loginLauncher.launch(intentState.value)
        }



}