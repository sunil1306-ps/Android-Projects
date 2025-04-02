package com.example.lemonade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lemonade.ui.theme.LemonadeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LemonadeTheme {
                // A surface container using the 'background' color from the theme
                LemonadeApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LemonadeApp(){
    LemonadeAppWithImages(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun LemonTextAndImage(
    imageResource: Int,
    stringResource: Int,
    contentDes: Int,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = stringResource(stringResource),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(painter = painterResource(imageResource),
            contentDescription = stringResource(contentDes),
            modifier = Modifier
                .clickable ( onClick = onImageClick )
                .border(
                    BorderStroke(2.dp, Color(105,205,216)),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(16.dp)
        )
    }
}

@Composable
fun LemonadeAppWithImages(modifier: Modifier = Modifier) {
    var currentStep by remember {mutableStateOf(1)}
    var count by remember { mutableStateOf(0)}
    when(currentStep){
        1 -> {
            LemonTextAndImage(
                imageResource = R.drawable.lemon_tree,
                stringResource = R.string.lemon_selection,
                contentDes = R.string.lemon_tree,
                onImageClick = {
                    currentStep = 2
                    count = (2..4).random()
                })
        }
        2 -> {
            LemonTextAndImage(
                imageResource = R.drawable.lemon_squeeze,
                stringResource = R.string.lemon_squeeze,
                contentDes = R.string.lemon,
                onImageClick = {
                    count--
                    if(count == 0){
                        currentStep = 3
                    }
                })
        }
        3 -> {
            LemonTextAndImage(
                imageResource = R.drawable.lemon_drink,
                stringResource = R.string.drink_lemonade,
                contentDes = R.string.glass_of_lemonade,
                onImageClick = { currentStep = 4 })
        }
        4 -> {
            LemonTextAndImage(
                imageResource = R.drawable.lemon_restart,
                stringResource = R.string.start_again,
                contentDes = R.string.empty_glass,
                onImageClick = { currentStep = 1 })
        }

    }
}
