package com.eslam.connectify.data.utils

import com.eslam.connectify.domain.models.Response
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun DatabaseReference.singleValueEvent(): Response<DataSnapshot> = suspendCoroutine { continuation ->
    val valueEventListener = object: ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            continuation.resume(Response.Error(error.message))
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            continuation.resume(Response.Success(snapshot))
        }
    }
    addListenerForSingleValueEvent(valueEventListener) // Subscribe to the event
}

suspend fun DatabaseReference.valueEventFlow(): Flow<Response<DataSnapshot>> = callbackFlow {
            trySend(Response.Loading)
    val valueEventListener = object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySendBlocking(Response.Success(snapshot))

        }
        override fun onCancelled(error: DatabaseError)
        {
            trySendBlocking(Response.Error(error.message))
        }

    }
    addValueEventListener(valueEventListener)
    awaitClose {
        removeEventListener(valueEventListener)
    }
}