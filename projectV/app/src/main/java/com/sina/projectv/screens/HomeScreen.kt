package com.sina.projectv.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sina.projectv.R
import com.sina.projectv.data.images
import com.sina.projectv.data.imageurl
import com.sina.projectv.ui.theme.ProjectVTheme
import com.sina.projectv.viewmodels.LoginViewModel
import com.sina.projectv.viewmodels.MediaViewModel
import com.sina.projectv.viewmodels.UserDataViewModel


val text = buildAnnotatedString {
    withStyle(style = SpanStyle(color = Color.Yellow)) {
        append("Project")
    }
    withStyle(style = SpanStyle(color = Color.White)) {
        append("V")
    }
}


@Composable
fun HomeScreen(
    viewModel: LoginViewModel,
    dataViewModel: UserDataViewModel,
    mediaViewModel: MediaViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val username by remember { derivedStateOf { dataViewModel.username } }
    val isDataLoading by dataViewModel.loadingState.collectAsState()


    LaunchedEffect(Unit) {
        dataViewModel.getUserData()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Unspecified),
            verticalArrangement = if (isDataLoading) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = if (isDataLoading) Alignment.CenterHorizontally else Alignment.Start
        ) {

            if (isDataLoading) {
                Box{
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {

                TopAppBar(viewModel, navController)

                Spacer(modifier = modifier.height(5.dp))

                Text(
                    text = "Welcome, $username"
                )

                Spacer(modifier = modifier.height(5.dp))

                ImageBanner(images = images)

                Spacer(modifier = modifier.height(10.dp))

                Text(
                    text = "Courses",
                    fontSize = 25.sp,
                    color = Color.White,
                    modifier = modifier.padding(start = 10.dp)
                )

                CourseCollage(mediaViewModel, navController)
            }

        }
    }
}


@Composable
fun CourseCollage(mediaViewModel: MediaViewModel, navController: NavHostController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(start = 5.dp, end = 5.dp)
    ) {
        Row (
            modifier
                .weight(0.5f)
                .fillMaxWidth()

        ){
            Box(
                modifier = modifier
                    .weight(0.5f)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Image 1",
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            navController.navigate("coursescreen")
                        }
                )
            }
            Box(
                modifier = modifier
                    .weight(0.5f)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Image 1",
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            navController.navigate("coursescreen")
                        }
                )
            }
        }
        Row (
            modifier
                .weight(0.5f)
                .fillMaxWidth()
        ){
            Box(
                modifier = modifier
                    .weight(0.5f)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Image 1",
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            navController.navigate("coursescreen")
                        }
                )
            }
            Box(
                modifier = modifier
                    .weight(0.5f)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Image 1",
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            navController.navigate("coursescreen")
                        }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageBanner(modifier: Modifier = Modifier, images: List<Int>) {
    val listState = rememberLazyListState()
    val flingBehavior: FlingBehavior = rememberSnapFlingBehavior(listState)

    val currentPage by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    Box(modifier = modifier) {
        // LazyRow for image scrolling
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(start = 10.dp, end = 10.dp)
        ) {
            items(images.size) { page ->
                Image(
                    painter = painterResource(images[page]),
                    contentDescription = "Image $page",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillParentMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }

    }
}





@Composable
fun TopAppBar(
    viewModel: LoginViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) }

    Column {
        Card(
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            ),
            modifier = modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.Unspecified),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    modifier = modifier.padding(start = 12.dp),
                    color = Color.White
                )
                Spacer(modifier = modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = modifier
                        .padding(end = 12.dp)
                        .size(26.dp)
                )
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person",
                    tint = Color.White,
                    modifier = modifier
                        .padding(end = 12.dp)
                        .size(26.dp)
                        .clickable {
//                            expanded = !expanded
                            viewModel.userSignOut()
                            navController.navigate("loginscreen") {
                                popUpTo("homescreen") {
                                    inclusive = true
                                }
                            }
                        }
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            modifier = modifier.fillMaxWidth(),
            enter = expandHorizontally(animationSpec = tween(durationMillis = 300)) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = shrinkHorizontally(animationSpec = tween(durationMillis = 300)) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            SideBar()
        }

    }
}

@Composable
fun SideBar(modifier: Modifier = Modifier) {

    Box(Modifier.fillMaxSize().background(Color.Transparent)) {
        Row(modifier.fillMaxSize()) {
            Column(modifier.weight(0.3f).fillMaxHeight().background(Color.Transparent)) {

            }
            Column(
                modifier = modifier
                    .weight(0.7f)
                    .fillMaxHeight()
                    .background(Color.Green)
            ) {

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ProjectVTheme {
//        HomeScreen()
    }
}
