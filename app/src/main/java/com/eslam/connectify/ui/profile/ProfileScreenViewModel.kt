package com.eslam.connectify.ui.profile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(): ViewModel() {

  private  var _nameState: MutableState<String> =  mutableStateOf("")

    val nameState: State<String>
    get() = _nameState


    fun getProfileName(name:String)
    {
        _nameState.value = name
    }


}