package com.eslam.connectify.domain.datasources

interface ChatRoomRepository {

    suspend fun sendMessage(msg:String)
}