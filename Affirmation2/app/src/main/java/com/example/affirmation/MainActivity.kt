package com.example.affirmation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.affirmation.data.Datasource
import com.example.affirmation.model.Affirmation
import com.example.affirmation.ui.theme.AffirmationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AffirmationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AffirmationApp()
                }
            }
        }
    }
}

@Composable
fun AffirmationApp() {
    val context = LocalContext.current
    AffirmationTheme {
        AffirmationList(affirmationList = Datasource().loadAffirmation())
    }
}

@Composable
fun AffirmationList(affirmationList: List<Affirmation>, modifier:Modifier = Modifier){
    LazyColumn {
        items(affirmationList) { affirmation ->
            AffirmationCard(affirmation)
        }
    }
}

@Composable
fun AffirmationCard(affirmation: Affirmation, modifier: Modifier = Modifier) {
    Card(modifier = Modifier.padding(8.dp), elevation = 4.dp) {
        Column {
            Image(
                painter = painterResource(affirmation.imageResourceId),
                contentDescription = stringResource(affirmation.stringResourceId),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = stringResource(affirmation.stringResourceId),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AffirmationApp()
}