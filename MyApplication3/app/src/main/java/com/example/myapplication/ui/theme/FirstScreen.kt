package com.example.myapplication.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.AppUiState
import com.example.myapplication.AppViewModel
import com.example.myapplication.Screens

@Composable
fun FirstScreen(
    navContoller: NavController,
    appViewModel: AppViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = { TopAppBar() }) {padding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier.weight(1f))
            OutlinedTextField(
                value = appViewModel.userName,
                onValueChange = {
                    appViewModel.userName = it
                }
            )
            Spacer(modifier.height(20.dp))
            Button(onClick = { navContoller.navigate(Screens.Second.name) }) {
                Text("Google")
            }
            Spacer(modifier.height(20.dp))
            Button(onClick = { navContoller.navigate(Screens.Second.name) }) {
                Text(text = "Apple")
            }
            Spacer(modifier.weight(1f))
        }
    }
}

@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .height(50.dp)
    ) {

    }
}

