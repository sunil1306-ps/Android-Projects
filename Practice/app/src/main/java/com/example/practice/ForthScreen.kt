package com.example.practice

import android.inputmethodservice.Keyboard
import android.net.ConnectivityManager.OnNetworkActiveListener
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.practice.ui.theme.Screens

@Composable
fun ForthScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Activity 4", fontSize = 32.sp, modifier = modifier.padding(top = 32.dp,bottom = 64.dp))
        Spacer(modifier = modifier.height(128.dp))
        Button(
            onClick = { navController.navigate(Screens.Third.name) }
        ) {
            Text(text = "Next")
        }
    }
}