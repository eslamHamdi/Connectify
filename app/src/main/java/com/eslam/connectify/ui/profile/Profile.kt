package com.eslam.connectify.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eslam.connectify.R
import com.eslam.connectify.ui.theme.ConnectifyTheme
import dagger.hilt.android.lifecycle.HiltViewModel


@Composable
fun ProfileScreen()
{
    val context = LocalContext.current

    val viewModel:ProfileScreenViewModel = hiltViewModel()

    ConnectifyTheme() {
        Surface(color = MaterialTheme.colors.primary, modifier = Modifier.fillMaxSize()) {

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp) ) {

                Card(shape = CircleShape, modifier = Modifier.wrapContentSize()) {

                    Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_avatar), contentDescription = "",
                        modifier = Modifier
                            .size(200.dp)
                            .clickable { }, contentScale = ContentScale.Crop)

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
                
                Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(0.7f), colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)) {
                    
                    Text(text = "Setup Profile", modifier = Modifier.wrapContentSize(align = Alignment.Center), color = Color.White)
                }


            }

        }
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProfileScreen()
}