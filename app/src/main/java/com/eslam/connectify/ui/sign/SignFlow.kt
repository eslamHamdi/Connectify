package com.eslam.connectify.ui.sign

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.hilt.navigation.compose.hiltViewModel
import com.eslam.connectify.data.repositories.SharedPreferencesRepositoryImpl
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.ui.destinations.ChannelListScreenDestination
import com.eslam.connectify.ui.destinations.ProfileScreenDestination
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
@Destination(start = true)
fun LoginScreen(navigator:DestinationsNavigator)
{
        val viewModel:SignViewModel = hiltViewModel<SignViewModel>()
    val context = LocalContext.current
    val preferences = SharedPreferencesRepositoryImpl(context)
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
                if ((signInState.value as Response.Success<String?>).data == null)
                {

                }else
                {
                    val id = (signInState.value as Response.Success<String?>).data
                    val state = id?.let { preferences.getAccountState(it) }

                    if (state == true)
                    {
                        navigator.navigate(ChannelListScreenDestination)
                    }else{
                        navigator.navigate(ProfileScreenDestination)
                    }



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

            if (intentState.value != null)
            {
                loginLauncher.launch(intentState.value)
            }

        }



}