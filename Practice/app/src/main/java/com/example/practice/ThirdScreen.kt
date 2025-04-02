package com.example.practice

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
import com.example.practice.ui.theme.Screens

@Composable
fun ThirdScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Activity 3", fontSize = 32.sp, modifier = modifier.padding(top = 32.dp,bottom = 64.dp))
        Spacer(modifier = modifier.height(128.dp))
        Row{
            Button(
                onClick = { navController.navigate(Screens.Forth.name) }
            ) {
                Text(text = "Next")

            }
            Spacer(modifier = modifier.padding(40.dp))
            Button(
                onClick = { navController.navigate(Screens.Second.name) }
            ) {
                Text(text = "Prev")
            }
        }
    }
}