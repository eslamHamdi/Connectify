package com.eslam.connectify.domain.datasources

import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.models.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {


    fun findContactByPhone(name:String): Flow<Response<List<ChatRoom?>>>

    fun addContact(userId: String):Flow<Boolean>

    fun addRoom(room:ChatRoom,contactId:String):Flow<Boolean>

    suspend fun getRooms():Flow<List<ChatRoom?>>

   suspend fun getRoomsDemo(): Response<List<ChatRoom?>>

    fun signOut():Flow<Boolean>
}