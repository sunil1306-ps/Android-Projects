package com.example.googlelogin.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun UserProfile(
    email: String? = null,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = "Welcome $email"
        )
        Spacer(modifier = modifier.height(50.dp))
        Button(onClick = {  }) {
            Text(text = "Back")
        }
    }
}
