package com.eslam.connectify.domain.models

import java.util.*

data class ChatMessage(val messageId:String,val content:String? = null,val senderId:String,val mediaUrl:String?,
val timeStamp:Long = Date().time)
