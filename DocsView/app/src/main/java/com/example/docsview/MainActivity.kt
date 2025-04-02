package com.example.docsview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.example.docsview.ui.theme.DocsViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DocsViewTheme {
                // A surface container using the 'background' color from the theme
                DocCard()
            }
        }
    }
}

@Composable
fun DocCard(modifier: Modifier = Modifier) {
    AsyncImage(
        model = "https://example.com/image.jpg",
        contentDescription = null
    )
}

