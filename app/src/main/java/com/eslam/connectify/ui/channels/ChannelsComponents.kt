package com.eslam.connectify.ui.channels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SearchBar(
    searchText: String,
    placeholderText: String = "",
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var showClearButton = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val showBackButton: MutableState<Boolean> = remember{ mutableStateOf(false)}



    TopAppBar(title = { Text("Connectify", modifier = Modifier.wrapContentWidth(align = Alignment.Start)) },
        navigationIcon = {
//        IconButton(onClick = { onNavigateBack() }) {
//            Icon(
//                imageVector = Icons.Filled.ArrowBack,
//                modifier = Modifier,
//                contentDescription = "stringResource(id = R.string.icn_search_back_content_description)"
//            )
//        }
    }, actions = {

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(vertical = 2.dp)
                .onFocusChanged { focusState ->
                    showClearButton.value = (focusState.isFocused)
                }
                .focusRequester(focusRequester),
            value = searchText,
            onValueChange = onSearchTextChanged,
            placeholder = {
                Text(text = placeholderText)
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                cursorColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = showClearButton.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = { onClearClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "stringResource(id = R.string.icn_search_clear_content_description)"
                        )
                    }

                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
        )


    })


//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//    }
}


@Composable
fun NoSearchResults() {

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text("No matches found")
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SearchBarUI(
    searchText: String,
    placeholderText: String = "",
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    matchesFound: Boolean,
    results: @Composable () -> Unit = {}
) {

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            SearchBar(
                searchText,
                placeholderText,
                onSearchTextChanged,
                onClearClick,
                onNavigateBack
            )

            if (matchesFound) {
                Text("Results", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                results()
            } else {
                if (searchText.isNotEmpty()) {
                    NoSearchResults()
                }

            }
        }

    }
}