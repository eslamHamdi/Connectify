package com.eslam.connectify.data.repositories

import android.app.Activity
import android.content.Context
import com.eslam.connectify.domain.datasources.SharedPreferencesRepository

class SharedPreferencesRepositoryImpl (private val context: Context):SharedPreferencesRepository {


    private val preferences = context.getSharedPreferences("AccountState",Context.MODE_PRIVATE)
    override fun saveAccountState(id: String, state: Boolean) {
        preferences.edit().putBoolean(id,state).apply()

    }

    override fun getAccountState(id: String): Boolean {
        return preferences.getBoolean(id,false)
    }


}