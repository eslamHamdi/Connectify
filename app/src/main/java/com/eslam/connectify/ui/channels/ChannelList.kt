package com.eslam.connectify.ui.channels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.eslam.connectify.R
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.ui.destinations.RoomMessagesScreenDestination
import com.eslam.connectify.ui.theme.ConnectifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Destination
@Composable
fun ChannelListScreen(navigator: DestinationsNavigator?)
{
//    TopBar(title = "Connectify", icon = Icons.Default.Home,viewModel) {
//        return@TopBar
//    }
    val viewModel:ChannelsViewModel = hiltViewModel()


    LaunchedEffect(key1 = Unit,)
    {
        viewModel.getAvailableChats()
    }
    Scaffold(topBar = {
        SearchBar(searchText = "")
    }

    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val rooms = viewModel.chatRooms
            Box {

                CircularProgressIndicator(modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(if (viewModel.loadingState.value) 1f else 0f))

                LazyColumn {


                    items(rooms.value.sortedByDescending {
                        it?.lastMessage?.timeStamp
                    }, key = {chatRoom->
                        chatRoom?.id!!
                    }){ room->
                        if (room != null) {

                            ContactItem(navigator,room, )
                        }
                    }
                }


            }


        }
    }

}

  @ExperimentalUnitApi
  @ExperimentalComposeUiApi
  @ExperimentalAnimationApi
  @Composable
  fun ContactItem(navigator: DestinationsNavigator?,room:ChatRoom)
  {
     
      Card(modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight(align = Alignment.CenterVertically)
          .padding(4.dp)
          .clickable { navigator?.navigate(RoomMessagesScreenDestination(room)) },
      elevation = 8.dp, shape = CutCornerShape(topEnd = 18.dp), border = BorderStroke(2.dp,MaterialTheme.colors.secondary),) {
          Row(
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                  .fillMaxWidth()
                  .wrapContentHeight()) {
              
              ProfilePic(imgSource = room.imageUrl)

              Spacer(modifier = Modifier.padding(4.dp))

              Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {

                  Text(text = room.name!!)
                  Text(text = when(room.lastMessage?.type){

                      Text -> room.lastMessage?.content!!
                      Photo -> "Photo"
                      Video -> "Video"
                      File -> "File Attachment"
                      else -> {""}
                  }, maxLines = 1)
              }





          }
      }

  }

   @Composable
  fun ProfilePic(imgSource:String?)
  {
      Card( shape = CircleShape,

          modifier = Modifier
              .wrapContentSize()
              .padding(8.dp)) {


          Image(painter = rememberImagePainter(data = imgSource?:R.drawable.ic_avatar,
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
fun TopBar(title:String, icon: ImageVector,viewModel:ChannelsViewModel, navAction:()->Unit)
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

        OutlinedTextField(value = viewModel.searchBarState.value, onValueChange ={
                 viewModel.contactSearch(it)
        } ,
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
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ConnectifyTheme() {
        ChannelListScreen(null)

    }

}

 const val Text = "Text"
const val Photo = "Photo"
const val Video = "Video"
const val File = "File"