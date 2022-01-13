package com.eslam.connectify.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(val uid:String? = null, var name:String?=null, var email:String?=null, var phone:String?=null,
                var profileImage:String? = null,var contacts:MutableList<User>?= mutableListOf()) :
    Parcelable

