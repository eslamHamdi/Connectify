package com.eslam.connectify.ui.profile

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.models.User
import com.eslam.connectify.domain.usecases.AccountSetupUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val useCases: AccountSetupUseCases): ViewModel() {

  private  var _nameState: MutableState<String> =  mutableStateOf("")

    val nameState: State<String>
    get() = _nameState

  private var _profileState:MutableState<String> =  mutableStateOf("")
  val profileState: State<String>
    get() = _profileState

  private var _errorState :MutableState<String?> =  mutableStateOf(null)
  val errorState: State<String?>
    get() = _errorState

  private var _loadingState :MutableState<Boolean> =  mutableStateOf(false)
  val loadingState: State<Boolean>
    get() = _loadingState

  private var _userState :MutableState<User> =  mutableStateOf(User())
  val userState: State<User>
    get() = _userState




    fun getProfileName(name:String)
    {
        _nameState.value = name
    }


    fun createProfile(img: Uri?,name: String)
    {
      viewModelScope.launch {

        _loadingState.value = true

        when(val response = useCases.createProfile(img,name))
        {
          is Response.Success -> {

            _profileState.value = response.data as String
            _loadingState.value = false

          }
          is Response.Error -> {

            _errorState.value = response.message
            _loadingState.value = false
          }

          else -> {}
        }






      }

    }

  fun getUserAuthenticationId() = useCases.getUserId()


  fun getCurrentUserInfo()
  {
    viewModelScope.launch {

      useCases.getCurrentUserInfo().collect {

        when(it)
        {
          is Response.Success -> {

            _userState.value = it.data!!
          }

          is Response.Error ->{ _errorState.value = it.message}

          else ->{}
        }
      }
    }
  }





}