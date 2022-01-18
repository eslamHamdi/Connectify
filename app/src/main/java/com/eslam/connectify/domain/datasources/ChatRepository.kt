package com.eslam.connectify.domain.datasources

import com.eslam.connectify.domain.models.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {


    fun findContactByName(name:String): Flow<List<User>>

    fun addContact(user: User):Flow<Boolean>

    fun addRoom():Flow<Boolean>

    fun getRooms():Flow<List<User>>
}