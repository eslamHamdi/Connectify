package com.eslam.connectify.domain.datasources

interface SharedPreferencesRepository {


    fun saveAccountState(id:String,state:Boolean)

    fun getAccountState(id:String):Boolean
}