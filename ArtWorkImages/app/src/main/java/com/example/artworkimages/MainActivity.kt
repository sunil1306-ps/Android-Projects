package com.example.artworkimages

import android.os.Bundle
import android.service.controls.Control
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artworkimages.ui.theme.ArtWorkImagesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtWorkImagesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    color = MaterialTheme.colors.background
                ) {
                    ArtSpaceApp()
                }
            }
        }
    }
}

@Composable
fun ArtImageWithTitle(
    imageSource: Int,
    titleSource: Int,
    detailSource: Int,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageSource),
            contentDescription = null,
            modifier = Modifier
                .padding(20.dp)
                .border(border = BorderStroke(2.dp, Color.Gray), shape = RectangleShape)
                .height(450.dp)
                .width(400.dp)
                .shadow(elevation = 4.dp, shape = RectangleShape)
                .padding(20.dp)
        )
        
        Card(modifier = Modifier.padding(horizontal = 20.dp, vertical = 100.dp), elevation = 6.dp) {
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(titleSource),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(detailSource),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ArtSpaceApp(){
    var result by remember {
        mutableStateOf(0)
    }

    when (result) {
        1 -> ArtImageWithTitle(
            imageSource = R.drawable.bourgeois,
            titleSource = R.string.LouiseBourgeois,
            detailSource = R.string.TBourgeois
        )
        2 -> ArtImageWithTitle(
            imageSource = R.drawable.hockney,
            titleSource = R.string.DavidHockney,
            detailSource = R.string.THockney
        )
        3 -> ArtImageWithTitle(
            imageSource = R.drawable.imagechristina,
            titleSource = R.string.AndrewWyeth,
            detailSource = R.string.TWyeth
        )
        4 -> ArtImageWithTitle(
            imageSource = R.drawable.koons,
            titleSource = R.string.JeffKoons,
            detailSource = R.string.TKoons
        )
        5 -> ArtImageWithTitle(
            imageSource = R.drawable.lovers,
            titleSource = R.string.ReneMagritte,
            detailSource = R.string.TMagritte
        )
        else -> {ArtImageWithTitle(
            imageSource = R.drawable.memorysalvordali,
            titleSource = R.string.SalvadorDalí,
            detailSource = R.string.TDalí
        )
        if(result > 6)
            result = 1
        else if(result < 1)
            result = 6
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(bottom = 30.dp)
    ) {
        Button(
            onClick = { result-- },
            modifier = Modifier
                .width(150.dp).height(50.dp),
        ) {
            Text(text = "Prev")
        }
        Button(
            onClick = { result++ },
            modifier = Modifier
                .width(150.dp).height(50.dp)
        ) {
            Text(text = "Next")
        }
    }
    
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ArtWorkImagesTheme {
        ArtSpaceApp()
    }
}