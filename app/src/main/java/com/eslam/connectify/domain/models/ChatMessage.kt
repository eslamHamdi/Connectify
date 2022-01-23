package com.eslam.connectify.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ChatMessage(val messageId:String? =null,val content:String? = null,val senderId:String? = null,val mediaUrl:String? = null,
val timeStamp:Long? = null, val type:String? = null) : Parcelable
