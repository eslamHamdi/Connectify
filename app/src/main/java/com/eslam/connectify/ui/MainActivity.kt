package com.eslam.connectify.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eslam.connectify.data.repositories.SharedPreferencesRepositoryImpl
import com.eslam.connectify.ui.theme.ConnectifyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {


            ConnectifyTheme {
//                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.primary) {

                    ConnectifyApp()

                }
            }
        }
    }
}

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