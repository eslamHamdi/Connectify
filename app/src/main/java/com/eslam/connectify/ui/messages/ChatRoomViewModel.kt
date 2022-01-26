package com.eslam.connectify.ui.messages

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.connectify.domain.models.ChatMessage
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.usecases.ChatRoomUseCases
import com.eslam.connectify.ui.channels.Text
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(private val chatUseCases:ChatRoomUseCases,savedStateHandle: SavedStateHandle):ViewModel() {

    private var _messagesState: MutableState<List<ChatMessage>> = mutableStateOf(listOf())

    val messagesState:State<List<ChatMessage>>
    get() = _messagesState

    val roomId = savedStateHandle.get<ChatRoom>("room")?.id



    init {
        Log.e(null, "RoomId: $roomId ", )

    }
    fun getMessages(roomId:String) {


               viewModelScope.launch {
            chatUseCases.getRoomMessages(roomId).collect{
                when(it)
                {
                    is Response.Success ->{
                        if (_messagesState.value.isNotEmpty())
                        {
                            var newMessages:MutableList<ChatMessage>? = mutableListOf()
                             it.data?.forEach { msg->
                                _messagesState.value.forEach { chatMsg->
                                 if (msg.timeStamp != chatMsg.timeStamp)
                                 {
                                     newMessages?.add(msg)
                                 }

                                }
                            }
                            _messagesState.value+newMessages
                            Log.d(null, "getMessages:${_messagesState.value} ", )
                        }else
                        {
                            _messagesState.value = it.data!!
                        }
                    }

                    is Response.Error ->{}

                    is Response.Loading ->{}

                    else ->{}
                }
            }
        }


   }



    fun getSender() = chatUseCases.getSenderId()




    fun sendingMsg(message:String,room: ChatRoom)
    {
        val chatMessage:ChatMessage = ChatMessage(message,getSender(),null,Date().time, Text)
        viewModelScope.launch {
            chatUseCases.sendMessage(chatMessage,room)
        }
    }
}