package com.study.prototype1

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.prototype1.R
import com.study.prototype1.Data.DataState
import com.study.prototype1.Data.Image
import com.study.prototype1.downloadmanagerguide.AndroidDownloader
import com.study.prototype1.ui.theme.Prototype1Theme
import com.study.prototype1.viewModels.MainViewModel

@Composable
fun SetData(viewModel: MainViewModel, downloader: AndroidDownloader, context: Context, navController: NavHostController, modifier: Modifier = Modifier) {
    when ( val result = viewModel.response.value) {
        is DataState.Loading -> {
            Box(
                modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }
        is DataState.Success -> {
            ShowLazyList(result.data, downloader, context, navController)
        }
        is DataState.Failure -> {
            Box(
                modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = result.message)
            }
        }
        else -> {
            Box(
                modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error Fetching Data")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowLazyList(images: MutableList<Image>, downloader: AndroidDownloader, context: Context, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(navController)
        }
    ) {
        LazyColumn(
            Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.Unspecified)
        ) {
            items(items = images) {image ->
                CardItem(image = image, downloader, context)
            }
        }
    }
}

@Composable
fun TopAppBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.tertiary)
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (
            (navController.currentDestination.toString().substringAfterLast("=") == "year") || (navController.currentDestination.toString().substringAfterLast("=") == "sign_in")) {
            Spacer(modifier = modifier.width(10.dp))
            Text(
                text = "StudyHUB",
                color = MaterialTheme.colorScheme.outline,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.align(Alignment.CenterVertically)
            )

        } else {
//            Icon(
//                painter = painterResource(id = R.drawable.logo_green),
//                contentDescription = "logo",
//                modifier = modifier.fillMaxHeight().align(Alignment.Bottom),
//                tint = MaterialTheme.colorScheme.outline
//            )
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = modifier
                    .size(40.dp)
                    .padding(start = 10.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = modifier.weight(0.40f))
            Text(
                text = "StudyHUB",
                color = MaterialTheme.colorScheme.outline,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = modifier.weight(0.60f))
        }

    }
}

@Composable
fun CardItem(
    image: Image,
    downloader: AndroidDownloader,
    context: Context,
    modifier: Modifier = Modifier
) {

    ElevatedCard(
        modifier = modifier
            .height(200.dp)
            .width(300.dp)
            .padding(start = 20.dp, top = 15.dp, bottom = 15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(
            modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable._c0eaf7dacdf43c6b943fd1387490960),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    //.wrapContentSize()
                    .fillMaxSize()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(image.image!!.toUri(), "image/jpg")
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        val intentChooser = Intent.createChooser(intent, "Choose app...")
                        try {
                            context.startActivity(intentChooser)
                        } catch (e: ActivityNotFoundException) {
                            Toast
                                .makeText(context, "No app found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(20.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${image.name}",
                    color = MaterialTheme.colorScheme.outline,
                    modifier = modifier.padding(start = 10.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "download",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = modifier
                        .padding(end = 10.dp)
                        .size(40.dp)
                        .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(125.dp))
                        .clickable {
                            downloader.downloadFile("${image.name}", "${image.image}")
                        }
                )

            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun CardItemPreview(modifier: Modifier = Modifier) {
    Prototype1Theme {
        Card(
            modifier = modifier
                .height(200.dp)
                .width(300.dp)
                .padding(top = 15.dp, bottom = 15.dp)
                .background(Color.Cyan),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 50.dp
            )
        ) {
            Box(
                modifier
                    .height(200.dp)
                    .width(300.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable._c0eaf7dacdf43c6b943fd1387490960),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        //.wrapContentSize()
                        .fillMaxSize()
                )
                Row(
                    modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.LightGray)
                        .align(Alignment.BottomCenter)
                        .shadow(elevation = 20.dp, shape = RoundedCornerShape(20.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Image",
                        color = Color.Black,
                        modifier = modifier.padding(start = 10.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_downward_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = modifier
                            .padding(end = 10.dp)
                            .size(40.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(125.dp))
                            .clickable {

                            }
                    )

                }
            }
        }
    }
}











