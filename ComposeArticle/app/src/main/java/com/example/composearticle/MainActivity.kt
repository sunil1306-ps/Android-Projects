package com.example.composearticle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composearticle.ui.theme.ComposeArticleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeArticleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    display()
                }
            }
        }
    }
}



@Composable
fun display(modifier: Modifier = Modifier){
    val image = painterResource(R.drawable.bg_compose_background)
    val title = stringResource(R.string.title_text)
    val para_1 = stringResource(R.string.para1_text)
    val para_2 = stringResource(R.string.para2_text)
    Column (modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = title,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = para_1,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
        Text(
            text = para_2,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeArticleTheme {
        display()
    }
}