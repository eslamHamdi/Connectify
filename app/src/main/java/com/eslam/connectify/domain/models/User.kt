package com.eslam.connectify.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class User(var uid:String? = null, var name:String?=null, var phone:String?=null,
                var profileImage:String? = null, var contacts: Map<*,*>? = null)


data class Contacts(val contactList:List<Map<String,String>>? = null)

