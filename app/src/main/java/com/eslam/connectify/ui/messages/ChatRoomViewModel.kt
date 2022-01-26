package com.eslam.connectify.ui.messages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.eslam.connectify.domain.models.ChatMessage
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.usecases.ChatRoomUseCases
import com.eslam.connectify.ui.channels.Text
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(private val chatUseCases:ChatRoomUseCases,savedStateHandle: SavedStateHandle):ViewModel() {

    @SuppressLint("MutableCollectionMutableState")
     private var _messagesState: MutableStateFlow<List<ChatMessage>> = MutableStateFlow(
        listOf())

    val messagesState: StateFlow<List<ChatMessage>>
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
                    is Response.Success -> {
                        if (_messagesState.value.isNotEmpty()!!) {

                            val newList = it.data?.filter { msg->
                                msg.timeStamp!! > _messagesState.value.lastOrNull()?.timeStamp!!
                            }
                            _messagesState.emit(_messagesState.value+newList!!)
//                            Log.d(null, "getMessages:${newList} ",)
//
//                            var newMessages: MutableList<ChatMessage>? = mutableListOf()
//
//
//                            it.data?.forEach { msg ->
//                                _messagesState.value.forEach { chatMsg ->
//                                    if (msg.timeStamp != chatMsg.timeStamp) {
//                                        newMessages?.add(msg)
//                                    }
//
//                                }
//                            }
//                            if (newMessages != null) {
//                               // Log.e(null, "getMessages: $newMessages",)
//
//
//                            }
                        }else {
                            _messagesState.emit(it.data!!)
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