package com.eslam.connectify.ui.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


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


@Composable
@Preview
fun DefaultPreview() {
 Message("Hiiiii",false)
}