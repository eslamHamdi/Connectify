package com.eslam.connectify.ui.messages

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.ui.channels.ProfilePic
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Composable
 fun Message(msg:String,received:Boolean)
 {
     val msgColorState: MutableState<Color> = remember{ mutableStateOf(Color(0xFFFF9800))}
     val msgAlignmentState:MutableState<Alignment> = remember{ mutableStateOf(Alignment.TopEnd)}

     if(received)
     {
         msgColorState.value = Color(0xFF8BC34A)
         msgAlignmentState.value = Alignment.TopStart
     }

     Surface(modifier = Modifier
         .fillMaxWidth()
         .wrapContentHeight()
         .padding(4.dp)) {
         Card(
             shape = RoundedCornerShape(8.dp),
             modifier = Modifier
                 .wrapContentSize(align = msgAlignmentState.value)
                 .padding(4.dp)
             , backgroundColor = msgColorState.value
         ) {
             Column() {
                 Text(text = msg, color = Color.White,
                     style = MaterialTheme.typography.body2, modifier = Modifier
                         .wrapContentSize(Alignment.TopStart)
                         .padding(4.dp))
                 //Image(imageVector = , contentDescription = ,)
             }

         }
     }

 }



  @ExperimentalUnitApi
  @ExperimentalComposeUiApi
  @ExperimentalAnimationApi
  @Composable
  fun MessagesAppBar(room:ChatRoom,contactState:String="",navigator: DestinationsNavigator?)
  {
     TopAppBar(
          {

              Icon(imageVector = Icons.Default.ArrowBack , contentDescription = "", modifier = Modifier.clickable {
                  navigator?.navigateUp()
              })
              ProfilePic(imgSource = room.imageUrl)
              Column() {
                  Text(text =room.name?:"Eslam", modifier = Modifier.wrapContentSize())
                  Text(text =contactState)
              }




          })





  }


@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
@Preview
fun DefaultPreview() {
 //Message("Hiiiii",false)
    MessagesAppBar(room = ChatRoom(), contactState ="online" , navigator = null)
}