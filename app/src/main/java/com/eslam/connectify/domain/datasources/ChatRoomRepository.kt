package com.eslam.connectify.domain.datasources

import com.eslam.connectify.domain.models.ChatMessage
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import kotlinx.coroutines.flow.Flow

interface ChatRoomRepository {

    suspend fun sendMessage(msg:ChatMessage,room: ChatRoom)

   suspend fun getRoomMessages(roomId:String):Flow<Response<List<ChatMessage>?>?>

   fun getSenderId():String?
}