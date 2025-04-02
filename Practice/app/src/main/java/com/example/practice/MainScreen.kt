package com.example.practice

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.practice.ui.theme.PracticeTheme


@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PracticeTheme {
        MainScreen()
    }
}