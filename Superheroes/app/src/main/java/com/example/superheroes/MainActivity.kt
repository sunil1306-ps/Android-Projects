package com.example.superheroes

import android.os.Bundle
import android.transition.Fade
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.Spring.StiffnessVeryLow
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.superheroes.data.Superhero
import com.example.superheroes.data.superheroes
import com.example.superheroes.ui.theme.SuperheroesTheme
import javax.sql.DataSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperheroesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SuperheroApp()
                }
            }
        }
    }
}


@Composable
fun SuperheroApp(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar()
        }
    ) {padding ->
        LazyColumn() {
            items(superheroes) { superhero ->
                SuperheroItem(superhero)
                modifier.padding(padding)
            }
        }
    }
}

@Composable
fun SuperheroItem(
    superhero: Superhero,
    modifier: Modifier = Modifier
) {
    Card(elevation = 10.dp,
        modifier = modifier
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(superhero.nameResourceId),
                    style = MaterialTheme.typography.h3)
                Text(text = stringResource(superhero.descriptionRes),
                    style = MaterialTheme.typography.body1)
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Box(modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))) {
                Image(
                    modifier = modifier
                        .fillMaxSize(),
                    painter = painterResource(superhero.imageResourceId),
                    contentDescription = superhero.nameResourceId.toString() + "Image",
                    alignment = Alignment.TopCenter,
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

}

@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.h1,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SuperheroAppPreview() {
    SuperheroesTheme(darkTheme = false) {
        SuperheroApp()
    }
}