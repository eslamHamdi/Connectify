package com.eslam.connectify.domain.usecases

import com.eslam.connectify.domain.datasources.ChatRepository
import com.eslam.connectify.domain.models.ChatRoom
import com.eslam.connectify.domain.models.User
import javax.inject.Inject

class ChatsScreenUseCases @Inject constructor(private val repository: ChatRepository) {


    fun searchForContacts(name:String) = repository.findContactByName(name)

   suspend fun getSavedChatRooms() = repository.getRooms()

    fun addRoom(chatRoom: ChatRoom,id:String) = repository.addRoom(chatRoom,id)

    fun addContactToList(userId: String) = repository.addContact(userId)


    fun signOut() = repository.signOut()

}