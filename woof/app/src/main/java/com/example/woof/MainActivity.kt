package com.example.woof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.woof.data.Dog
import com.example.woof.data.dogs
import com.example.woof.ui.theme.WoofTheme
import androidx.compose.animation.core.Spring as Spring

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WoofTheme {
                WoofApp()
            }
        }
    }
}

@Composable
fun WoofApp(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            WoofTopAppBar()
        }
    ) {
        LazyColumn(modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            items(dogs) {dog->
                DogItem(dog = dog)
            }
        }
    }
}

@Composable
fun DogItem(dog: Dog, modifier: Modifier = Modifier) {
    var expanded by remember {mutableStateOf(false)}
    Card(
        modifier = modifier.padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                    
                )
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
            ) {
                DogIcon(dogIcon = dog.imageResourceId)
                DogInformation(dogName = dog.name, dogAge = dog.age)
                Spacer(modifier = modifier.weight(1f))
                DogItemButton(
                    expanded = expanded,
                    onClick = {expanded = !expanded}
                )
            }
            if(expanded) {
                DogHobby(dogHobby = dog.hobbies)
            }
        }
    }
}

@Composable
fun DogHobby(@StringRes dogHobby: Int, modifier: Modifier = Modifier) {
    Column(modifier.padding(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp,
            bottom = 16.dp
        )
    ) {
        Text(
            text = stringResource(id = R.string.about),
            style = MaterialTheme.typography.h3
        )
        Text(
            text = stringResource(id = dogHobby),
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun DogInformation(
    @StringRes dogName: Int,
    dogAge: Int,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(dogName),
            Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.h2,
        )
        Text(
            text = stringResource(id = R.string.years_old, dogAge),
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun DogIcon(@DrawableRes dogIcon: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = Modifier
            .size(64.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(50)),
        contentScale = ContentScale.Crop,
        painter = painterResource(dogIcon),
        contentDescription = null
    )
}

@Composable
fun DogItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if(expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(R.string.expand_button_content_description)
        )
    }
}

@Composable
fun WoofTopAppBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = modifier
                .size(64.dp)
                .padding(8.dp),
            painter = painterResource(R.drawable.ic_woof_logo), 
            contentDescription = "woof logo"
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.h1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WoofTheme(darkTheme = false) {
        WoofApp()
    }
}