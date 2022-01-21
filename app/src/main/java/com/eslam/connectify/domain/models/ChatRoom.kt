package com.eslam.connectify.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ChatRoom(val id:String? =null,val lastMessage:ChatMessage? = null,
                    val imageUrl:String?=null,val name:String?=null,
val messages:List<ChatMessage>? = null) : Parcelable
