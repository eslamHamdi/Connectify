package com.eslam.connectify.ui.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslam.connectify.R
import com.eslam.connectify.data.repositories.SharedPreferencesRepositoryImpl
import com.eslam.connectify.ui.destinations.ChannelListScreenDestination
import com.eslam.connectify.ui.theme.ConnectifyTheme
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Destination
@Composable
fun ProfileScreen(navigator: DestinationsNavigator?)
{
    val context = LocalContext.current
    val viewModel = hiltViewModel<ProfileScreenViewModel>()
    val preferences = SharedPreferencesRepositoryImpl(context)
    val imageBitmap:MutableState<Bitmap?> = remember{ mutableStateOf<Bitmap?>(AppCompatResources.getDrawable(context, R.drawable.ic_avatar)
        ?.toBitmap())}
    val imageUri = remember {
        mutableStateOf<Uri?>(null)
    }



    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri.value = uri
        imageUri.value?.let {
            if (Build.VERSION.SDK_INT < 28) {
                imageBitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                imageBitmap.value = ImageDecoder.decodeBitmap(source)
            }
        }
    }
        if (viewModel.profileState.value.isNotBlank() || viewModel.profileState.value.isNotEmpty() )
            {
                LaunchedEffect(key1 = Unit){Toast.makeText(context,viewModel.profileState.value,Toast.LENGTH_SHORT).show()
                }
                val id = viewModel.getUserAuthenticationId()
                if (id != null)
                {
                    preferences.saveAccountState(id,true)
                }

                navigator?.navigate(ChannelListScreenDestination){
                   popUpTo("ChannelListScreen")
                }



            }

           if (viewModel.errorState.value != null)
           {
               LaunchedEffect(key1 = Unit){Toast.makeText(context,viewModel.errorState.value,Toast.LENGTH_SHORT).show()
               }
           }

        Surface(color = MaterialTheme.colors.primary, modifier = Modifier.fillMaxSize()) {

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp) ) {

                Card(shape = CircleShape, modifier = Modifier.wrapContentSize()) {

                    Image(bitmap = imageBitmap.value?.asImageBitmap()!!, contentDescription = "",
                        modifier = Modifier
                            .size(200.dp)
                            .clickable {
                                launcher.launch("image/*")
                            }, contentScale = ContentScale.Crop)

                }

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp))

                Text(text = "Profile Information", modifier = Modifier.wrapContentSize(align = Alignment.Center))
                Text(text = "Please set your profile name and an optional photo", modifier = Modifier.wrapContentSize(align = Alignment.Center))

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp))

                TextField(value = viewModel.nameState.value , onValueChange ={ name->

                    viewModel.getProfileName(name)

                } , placeholder = { Text(text = "Type Your Name", modifier = Modifier.alpha(0.3f))},
                shape = RoundedCornerShape(20.dp))
                
                Button(onClick = {
                    if (viewModel.nameState.value.isEmpty() || viewModel.nameState.value.isBlank())
                    {
                        Toast.makeText(context,"Please Enter Profile Name",Toast.LENGTH_SHORT).show()
                    }else{
                        viewModel.createProfile(imageUri.value,viewModel.nameState.value)
                    }
                     }, modifier = Modifier.fillMaxWidth(0.7f), colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)) {
                    
                    Text(text = "Setup Profile", modifier = Modifier.wrapContentSize(align = Alignment.Center), color = Color.White)
                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp))

                CircularProgressIndicator(modifier=Modifier.alpha(
                    if (viewModel.loadingState.value) 1f else 0f
                ))


            }

        }



}



@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProfileScreen(null)
}