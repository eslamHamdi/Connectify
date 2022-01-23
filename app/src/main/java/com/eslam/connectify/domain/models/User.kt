package com.eslam.connectify.domain.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(  var uid:String? = null,var name:String?=null, var phone:String?=null,
                var profileImage:String? = null, var contacts:List<String>?= null) :
    Parcelable

