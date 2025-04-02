package com.example.scrollview

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrollview.data.DataState
import com.example.scrollview.data.Image
import com.example.scrollview.downlaodmanagerguide.AndroidDownloader
import com.example.scrollview.downlaodmanagerguide.DownloadCompletedReceiver
import com.example.scrollview.ui.theme.ScrollViewTheme
import com.example.scrollview.viewModels.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val downloadCompleted = DownloadCompletedReceiver()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        registerReceiver(
            downloadCompleted,
            IntentFilter(
                "android.intent.action.DOWNLOAD_COMPLETE"
            )
        )

        super.onCreate(savedInstanceState)
        setContent {
            ScrollViewTheme {
                Column {
                    TopAppBar()
                    SetData(viewModel)
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun SetData(viewModel: MainViewModel, modifier: Modifier = Modifier) {
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
                ShowLazyList(result.data)
            }
            is DataState.Failure -> {
                Box(
                    modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = result.message, fontSize = MaterialTheme.typography.h5.fontSize)
                }
            }
            else -> {
                Box(
                    modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error Fetching Data", fontSize = MaterialTheme.typography.h5.fontSize)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun ShowLazyList(images: MutableList<Image>) {
        LazyColumn(
            Modifier.padding(start = 30.dp)
                .fillMaxSize()
        ) {
            items(items = images) {image ->
                CardItem(image = image)
            }
        }
    }

    @Composable
    fun TopAppBar(
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primaryVariant)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Images",
                color = Color.White,
                fontFamily = MaterialTheme.typography.h1.fontFamily,
                fontSize = 30.sp,
                modifier = modifier.padding(start = 30.dp)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun CardItem(
        image: Image,
        modifier: Modifier = Modifier
    ) {
        val downloader = AndroidDownloader(this)
        val downloadCompleted = DownloadCompletedReceiver()
        Card(
            elevation = 10.dp,
            modifier = modifier
                .height(200.dp)
                .width(300.dp)
                .padding(top = 15.dp, bottom = 15.dp)
        ) {
            Box(
                modifier
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_task_completed),
                    contentDescription = null,
                    modifier
                        .wrapContentSize()
                        .fillMaxSize()
                )
                Row(
                    modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(MaterialTheme.colors.primaryVariant)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${image.name}",
                        fontFamily = MaterialTheme.typography.h2.fontFamily,
                        fontSize = MaterialTheme.typography.h2.fontSize,
                        color = Color.White,
                        modifier = modifier.padding(start = 10.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_circle_down_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = modifier
                            .padding(end = 10.dp)
                            .size(40.dp)
                            .clickable {
                                downloader.downloadFile("${image.name}","${image.image}")
                            }
                    )
                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadCompleted)
    }

}


