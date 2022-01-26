package com.eslam.connectify.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ChatRoom(val id:String? =null, var lastMessage:ChatMessage? = null,
                    var imageUrl:String?=null, var name:String?=null, ) : Parcelable
