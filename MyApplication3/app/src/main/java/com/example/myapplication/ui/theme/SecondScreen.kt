package com.example.myapplication.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.AppViewModel
import com.example.myapplication.R
import com.example.myapplication.Screens
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun SecondScreen(
    navController: NavController,
    appViewModel: AppViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(navController = navController) }
    ) {
        Column(
            modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Username: ${ appViewModel.userName }")
        }
    }
}

@Composable
fun TopAppBar(modifier: Modifier = Modifier, navController: NavController) {
    Row(
        modifier.fillMaxWidth()
            .background(Color.Blue)
            .height(50.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_arrow_back_24),
            contentDescription = null,
            modifier.clickable { navController.navigate(Screens.First.name) }
                .size(40.dp)
                .padding(start = 10.dp)
        )
    }
}










