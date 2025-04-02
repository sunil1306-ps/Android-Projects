package com.example.myapplication5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.size.Size
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.myapplication5.ui.theme.MyApplication5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplication5Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val url = "https://firebasestorage.googleapis.com/v0/b/docs-view.appspot.com/o/IT%20syllabus.pdf?alt=media&token=87e8a3d7-1707-4bfb-b5b1-efca7fb69a4e"
    Box(

    ) {
        GlideImage(
            model = url,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplication5Theme {
        Greeting()
    }
}