package com.eslam.connectify.domain.usecases

import com.eslam.connectify.domain.datasources.ChatRoomRepository
import com.eslam.connectify.domain.models.ChatMessage
import com.eslam.connectify.domain.models.ChatRoom
import javax.inject.Inject

class ChatRoomUseCases @Inject constructor(private val repository:ChatRoomRepository) {


    suspend fun sendMessage(msg:ChatMessage,room: ChatRoom)=repository.sendMessage(msg,room)

     suspend fun getRoomMessages(roomId:String) = repository.getRoomMessages(roomId)

    fun getSenderId() = repository.getSenderId()


    suspend fun sendUserStatus(state:String,roomId: String)= repository.sendUserActivityState(state,roomId)
    fun getContactState(contactId: String)= repository.getContactStatus(contactId)

    suspend fun getContactTypingState(roomId: String)=repository.getContactTypingState(roomId)
}