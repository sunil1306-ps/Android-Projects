package com.example.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practice.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PracticeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar()
        }
    ) {
        Column(
            modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.LightGray),
            verticalArrangement = Arrangement.Center,
        ) {
            CardComp("Hai")
            CardComp("Hello")
            CardComp("This is composable")
        }
    }
}

////////////////////////////////
@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Top Bar",
            fontSize = 20.sp
        )
    }
}

@Composable
fun CardComp(
    txt: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier
            .height(150.dp)
            .padding(10.dp),
        backgroundColor = Color.Cyan,
        elevation = 10.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        CardContent(txt = txt)
    }
}
////////////////////////////////////////////////////////////////////
@Composable
fun CardContent(txt: String,modifier: Modifier = Modifier) {
    Row(modifier
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_image_24),
            contentDescription = null,
            modifier
                .height(150.dp)
                .width(150.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = txt,
            fontSize = 30.sp,
            color = Color.Magenta
        )
    }
}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PracticeTheme {
        HomeScreen()
    }
}