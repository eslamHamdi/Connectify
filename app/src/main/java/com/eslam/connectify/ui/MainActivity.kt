package com.eslam.connectify.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eslam.connectify.ui.channels.ChannelListScreen
import com.eslam.connectify.ui.sign.LoginScreen
import com.eslam.connectify.ui.theme.ConnectifyTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


//            ConnectifyTheme {
//                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.primary) {
//
                    ConnectifyApp()
//
//                }
            }
        }
    }
}

@Composable
fun ConnectifyApp() {

    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginScreen(scaffoldState = scaffoldState){
                navController.navigate(route = "ChannelList"){
                    popUpTo("ChannelList")
                }
            } }
            composable("ChannelList") { ChannelListScreen() }

        }
    }







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