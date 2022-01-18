package com.eslam.connectify.ui.channels

import android.graphics.drawable.AnimatedVectorDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.eslam.connectify.R
import com.eslam.connectify.domain.models.User
import com.eslam.connectify.ui.theme.ConnectifyTheme
import com.ramcosta.composedestinations.annotation.Destination

@ExperimentalComposeUiApi
@Destination
@Composable
fun ChannelListScreen(contacts:List<User> = listOf())
{
    Scaffold(topBar = {
        TopBar(title = "Connectify", icon = Icons.Default.Home) {
            return@TopBar Unit
        }
    }

    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val type = LastMessageType.Text
            type.content = "Hiiiii"
            LazyColumn {
                items(contacts){ contact->
                    ContactItem({  },contact.profileImage?:"",contact.name!!,type)
                }
            }

        }
    }

}

  @Composable
  fun ContactItem(onClick:()->Unit,imgSource:String,name:String,lastMsg:LastMessageType)
  {
     
      Card(modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight(align = Alignment.CenterVertically)
          .padding(4.dp),
      elevation = 8.dp, shape = CutCornerShape(topEnd = 18.dp), border = BorderStroke(2.dp,MaterialTheme.colors.secondary)) {
          Row(
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                  .fillMaxWidth()
                  .wrapContentHeight()) {
              
              ProfilePic(imgSource = imgSource)

              Spacer(modifier = Modifier.padding(4.dp))

              Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {

                  Text(text = name)
                  Text(text = when(lastMsg){

                      LastMessageType.Text -> lastMsg.content!!
                      LastMessageType.Photo -> "Photo"
                      LastMessageType.Video -> "Video"
                      LastMessageType.File -> "File Attachment"
                  })
              }





          }
      }

  }

   @Composable
  fun ProfilePic(imgSource:String)
  {
      Card( shape = CircleShape,

          modifier = Modifier
              .wrapContentSize()
              .padding(8.dp)) {


          Image(painter = rememberImagePainter(data = imgSource,
              builder = {
                  crossfade(true)
                  transformations(CircleCropTransformation())
                  placeholder(R.drawable.ic_avatar)
              }
          ), contentDescription = "",
              contentScale = ContentScale.Crop,
              modifier = Modifier.size(50.dp))

      }
  }


@ExperimentalComposeUiApi
@Composable
fun TopBar(title:String, icon: ImageVector, navAction:()->Unit)
{
    //navigationIcon = {Icon(painter = rememberVectorPainter(image = icon ), contentDescription ="" , modifier = Modifier
    //        .padding(2.dp)
    //        .clickable { navAction.invoke() })
    //    },
    //
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    
    val iconState: MutableState<ImageVector> = remember{ mutableStateOf(Icons.Default.Search)}
    val searchBarsState: MutableState<Boolean> = remember{ mutableStateOf(false)}
    TopAppBar( title = { Text(
        text = title
    )}, actions = {

        OutlinedTextField(value = "", onValueChange ={} ,
             textStyle = TextStyle(color = Color.White), modifier = Modifier
                .alpha(
                    if (searchBarsState.value) 1f else 0f
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.7f)
        , singleLine = true, keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }))

        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            IconButton(onClick = {
                if (iconState.value == Icons.Default.Search )
                {
                    iconState.value = Icons.Default.Close
                    searchBarsState.value = true
                }else{iconState.value = Icons.Default.Search
                    searchBarsState.value = false}
            }) {
                Icon(painter = rememberVectorPainter(image = iconState.value ),contentDescription ="", modifier = Modifier.padding(4.dp))

            }
        }
        
    })

}


enum class LastMessageType(var content:String?) {
    Text(content = "null"), Photo(content = null), Video(content = null), File(content = null)
}
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ConnectifyTheme() {
        ChannelListScreen(listOf(User(name = "John Smith"),User(name = "John Smith"),
            User(name = "John Smith"),User(name = "John Smith")))
    }

}