package com.eslam.connectify.ui.messages

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.eslam.connectify.R
import com.eslam.connectify.domain.models.ChatMessage
import com.eslam.connectify.domain.models.ChatRoom
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalUnitApi
@SuppressLint("MutableCollectionMutableState")
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
@Destination
fun RoomMessagesScreen(room: ChatRoom,navigator: DestinationsNavigator?)
{

    var viewModel:ChatRoomViewModel? = hiltViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    val messagesList:State<List<ChatMessage>> = viewModel?.messagesState?.collectAsState()!!
    val contactStatus:State<String> = remember {
        viewModel!!.contactState
    }




val messageState:MutableState<String> = remember{ mutableStateOf("") }
    val scope = rememberCoroutineScope()

    DisposableEffect(key1 = viewModel) {
        viewModel?.getMessages(room.id!!)

        onDispose {
            viewModel = null
        }
    }




    Scaffold(topBar = { MessagesAppBar(room = room, contactState =contactStatus.value , navigator = navigator!!)}) {

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {


            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)) {
                items(messagesList.value.toList()){ chatMessage->
                    Message(msg = chatMessage.content!!, received = (viewModel?.getSender() != chatMessage.senderId) )
                }
            }


            Row(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

                OutlinedTextField(value = messageState.value, onValueChange ={
                    messageState.value = it

                    if (it.isNotEmpty())
                    {
                        viewModel!!.sendUserTypingState("Typing",room.id!!,it)
                    }else
                    {
                        viewModel!!.sendUserTypingState("Online",room.id!!,it)
                    }



                },shape = RoundedCornerShape(8.dp),
                    modifier= Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.Bottom)
                        .padding(4.dp),
                    placeholder = { Text(text = "Messages")})

                   Card(shape = CircleShape, modifier = Modifier
                       .wrapContentSize()
                       .padding(4.dp)
                       .clickable {
                           viewModel?.sendingMsg(messageState.value, room)
                           messageState.value = ""
                           viewModel!!.sendUserTypingState("Online",room.id!!,"")
                       }) {
                       Icon(imageVector = Icons.Default.Send, contentDescription = "", modifier = Modifier
                           .size(40.dp)
                           .align(Alignment.CenterVertically))

                   }


            }

        }
    }


}

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun MessagesDefaultPreview() {
    RoomMessagesScreen(room = ChatRoom(name = "Eslam"), navigator = null)
}


fun <T> SnapshotStateList<T>.swapList(newList: List<T>){
    clear()
    addAll(newList)
}