package com.eslam.connectify.ui.channels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.usecases.ChatsScreenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(private val chatsScreenUseCases: ChatsScreenUseCases):ViewModel() {

    private var _chatRooms: MutableState<List<ChatRoom?>> = mutableStateOf(listOf())

    val chatRooms:State<List<ChatRoom?>>
    get() = _chatRooms

    private var _chatRoomsUpdated: MutableStateFlow<ChatRoom?> = MutableStateFlow(ChatRoom())

    val chatRoomsUpdated: StateFlow<ChatRoom?>
        get() = _chatRoomsUpdated




    private var _loadingState:MutableState<Boolean> = mutableStateOf(false)

    val loadingState:State<Boolean>
    get() = _loadingState

    private var _searchBarState:MutableState<String> = mutableStateOf("")

    val searchBarState:State<String>
    get() = _searchBarState

    private var job:Job? = null

    init {


      listenToUpdates()

    }

    fun contactSearch(name:String)
    {
        job?.cancel()
        _searchBarState.value = name
        if (name.isEmpty() || name.isBlank())
        {
            getAvailableChats()
        }

        job = viewModelScope.launch {
            delay(1500)

            chatsScreenUseCases.searchForContacts(name).collect {
                when(it)
                {
                    is Response.Success ->{_chatRooms.value = it.data
                        _loadingState.value = false}

                    is Response.Error -> {
                        Log.e(null, "contactSearch: ${it.message} ", )
                        _loadingState.value = false}

                    is Response.Loading -> {_loadingState.value = true}
                }
            }
        }
    }

    fun getAvailableChats()
    {
        viewModelScope.launch {
             chatsScreenUseCases.getChatsDemo().collect{
                 when(it)
                 {
                     is Response.Success ->{
                         _chatRooms.value = it.data
                         _loadingState.value = false
                     }

                     is Response.Loading ->{
                         _loadingState.value = true
                     }

                     is Response.Error ->{
                         Log.e(null, "getAvailableChats: ${it.message}", )
                         _loadingState.value = false
                     }
                 }
             }





        }

    }


       private fun listenToRooms()
       {
           viewModelScope.launch {
               chatsScreenUseCases.listenToAddedRooms().stateIn(viewModelScope).collect{response->
                   when(response)
                   {
                       is Response.Success ->{
                           val upDateList = _chatRooms.value.toMutableList()
                           if (response.data.isNotEmpty())
                           {
                               response.data.forEach {
                                   val id = it?.id
                                   upDateList.forEach { old->
                                       if (old?.id == id)
                                       {
                                           old?.apply {
                                               lastMessage = it?.lastMessage
                                               name = it?.name
                                               imageUrl = it?.imageUrl
                                           }
                                       }
                                   }
                               }

                               _chatRooms.value = upDateList.toList()
                               Log.e("newRoomUpdate", "listenToRooms: ${_chatRooms.value} ", )
                           }

                       }
                       else -> {}
                   }

               }


           }
       }




    private fun listenToUpdates()
    {
        viewModelScope.launch {
            chatsScreenUseCases.listenToRoomsChanges().collect{

                when(it)
                {
                    is Response.Success -> {
                        val newRoom = it.data
                        val newList = _chatRooms.value.toMutableList()
                        newList.forEach { oldRoom->
                            if (newRoom.id == oldRoom?.id)
                            {
                                oldRoom?.apply {
                                    lastMessage = newRoom?.lastMessage
                                    name = newRoom?.name
                                    imageUrl = newRoom?.imageUrl
                                }
                            }
                            Log.e("oldRoom", "listenToUpdates: $oldRoom ", )
                            _chatRooms.value = listOf()
                            _chatRooms.value = newList.toList()
                        }
                    }
                    else ->{}
                }
            }
        }
    }
}