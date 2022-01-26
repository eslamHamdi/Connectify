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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(private val chatsScreenUseCases: ChatsScreenUseCases):ViewModel() {

    private var _chatRooms: MutableState<List<ChatRoom?>> = mutableStateOf(listOf())

    val chatRooms:State<List<ChatRoom?>>
    get() = _chatRooms


    private var _loadingState:MutableState<Boolean> = mutableStateOf(false)

    val loadingState:State<Boolean>
    get() = _loadingState

    private var _searchBarState:MutableState<String> = mutableStateOf("")

    val searchBarState:State<String>
    get() = _searchBarState

    private var job:Job? = null

    init {
        getAvailableChats()


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

    private fun getAvailableChats()
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


        fun listenToRooms()
       {
           viewModelScope.launch {
               chatsScreenUseCases.listenToAddedRooms().collect{response->
                   when(response)
                   {
                       is Response.Success ->{
                           val upDateList = _chatRooms.value.toMutableList()

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

                           _chatRooms.value = upDateList
                       }
                       else -> {}
                   }
               }
           }
       }
}