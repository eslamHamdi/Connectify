package com.eslam.connectify.domain.usecases

import com.eslam.connectify.domain.datasources.ChatRepository
import com.eslam.connectify.domain.models.ChatRoom
import javax.inject.Inject

class ChatsScreenUseCases @Inject constructor(private val repository: ChatRepository) {


    suspend fun searchForContacts(phone:String) = repository.findContactByPhone(phone)


    suspend fun getChatsDemo()=repository.getRoomsDemo()


    suspend fun listenToAddedRooms() = repository.listenToRoomsAdded()

    suspend fun listenToRoomsChanges() = repository.listenToRoomChanges()

    suspend fun setUserInitialFinalState(state:String)=repository.setUserState(state)


    fun signOut() = repository.signOut()

}