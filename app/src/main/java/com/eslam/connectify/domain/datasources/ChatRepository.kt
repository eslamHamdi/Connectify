package com.eslam.connectify.domain.datasources

import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.models.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {


   suspend fun findContactByPhone(name:String): Flow<Response<List<ChatRoom?>>>

  suspend fun listenToRoomsAdded():Flow<Response<List<ChatRoom?>>>
  suspend fun listenToRoomChanges(): Flow<Response<ChatRoom>?>

   suspend fun getRoomsDemo(): Flow<Response<List<ChatRoom?>>>

   suspend fun setUserState(state:String)

    fun signOut():Flow<Boolean>
}