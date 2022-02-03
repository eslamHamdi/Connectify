package com.eslam.connectify.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.eslam.connectify.data.repositories.SharedPreferencesRepositoryImpl
import com.eslam.connectify.ui.theme.ConnectifyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject


@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalUnitApi
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataBase:FirebaseDatabase
    @Inject
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setContent {
//            val lifecycleOwner = LocalLifecycleOwner.current
//
//            LaunchedEffect(key1 = lifecycleOwner.lifecycle.currentState)
//            {
//                if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED)
//                {
//
//                }
//            }



            ConnectifyTheme {
//                // A surface container using the 'background' color from the theme

                    ConnectifyApp()


            }
        }
    }

    override fun onStop() {

        try {

            lifecycle.coroutineScope.launch {
                dataBase.reference.child("usersStates").child(auth.currentUser?.uid!!).setValue("Offline").await()
            }
        }catch (e:Exception)
        {
            Log.e(null, "onDestroy: failed", )
        }

        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        try {

            lifecycle.coroutineScope.launch {
                dataBase.reference.child("usersStates").child(auth.currentUser?.uid!!).setValue("Online").await()

            }
        }catch (e:Exception)
        {
            Log.e(null, "onStart: failed", )
        }

        super.onStop()
    }
    }




@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ConnectifyApp() {

    DestinationsNavHost(navGraph = NavGraphs.root)


//    val navController = rememberNavController()
//    //val scaffoldState = rememberScaffoldState()
//        NavHost(navController = navController, startDestination = "login") {
//            composable("login") { LoginScreen{
//                navController.navigate(route = "ChannelList"){
//                    popUpTo("ChannelList")
//                }
//            } }
//            composable("ChannelList") { ChannelListScreen() }
//
//
//            composable("Profile") { ProfileScreen() }
//
//
//        }








}



@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Surface(color = MaterialTheme.colors.primary) {
        ConnectifyApp()
//    ConnectifyTheme {
//
//    }
    }
}