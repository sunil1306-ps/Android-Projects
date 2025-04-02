package com.sina.projectv.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sina.projectv.data.imageurl
import com.sina.projectv.ui.theme.ProjectVTheme
import com.sina.projectv.viewmodels.MediaViewModel
import kotlin.reflect.KProperty

@Composable
fun CourseScreen(
    context: Context,
    mediaViewModel: MediaViewModel,
    navcontroller: NavHostController,
    modifier: Modifier = Modifier
) {

    val title = mediaViewModel.videoTitle
    val duration = mediaViewModel.videoDuration
    val error =  mediaViewModel.error
    val videoDetails = listOf(title, duration, error)
    val isDataLoading = mediaViewModel.isVideoDataLoading


//    if (isDataLoading) {
//        Box{
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.Center),
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//    } else {
//
//
//
//    }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            content = {
                items(5) { index ->
                    TopicCard(
                        onMediaClick = { mediaUrl ->
                            mediaViewModel.loadMediaItem(context, imageurl)
                            mediaViewModel.playMedia(context)
                        },
                        videoDetails,
                        navcontroller
                    )
                }
            },
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier.padding(6.dp)
        )
    }

}


@Composable
fun TopicCard(
    onMediaClick: (String) -> Unit,
    videoDetails: List<Any>,
    navcontroller: NavHostController,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TopicName",
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.padding(end = 12.dp).size(30.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(durationMillis = 300)) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 300)) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                for (i in 1..5) {
                    MediaItem(
                        onClick = { onMediaClick(imageurl) },
                        videoDetails,
                        navcontroller
                    )
                }
            }
        }
    }
}

@Composable
fun MediaItem(
    onClick: () -> Unit,
    videoDetails: List<Any>,
    navcontroller: NavHostController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
            .clickable {
                onClick()
                navcontroller.navigate("mediaplayerscreen")
            }
    ) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 20.dp, bottom = 20.dp)
            ) {
                Text(
                    text = "${videoDetails[0]}",
                    color = Color.Black
                )
                Text(
                    text =  "${videoDetails[0]}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Duration: ${videoDetails[1]}",
                        color = Color.Black
                    )

                    Text(
                        text = "Rating: 4.5",
                        color = Color.Black
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun CourseScreenPreview() {
    ProjectVTheme {
//        TopicCard()
    }
}