package com.example.firebasestorage


import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebasestorage.ui.theme.FirebaseStorageTheme
import com.skydoves.landscapist.glide.GlideImage

class MainActivity : ComponentActivity() {
    var imageUriState = mutableStateOf<Uri?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseStorageTheme {
                UploadImage()
            }
        }
    }
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {uri ->
        imageUriState.value = uri
    }


    @Composable
    fun UploadImage(modifier: Modifier = Modifier) {
        Column(
            modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            GlideImage(imageModel = { imageUriState.value })

            Button(
                onClick = { selectImageLauncher.launch("image/*") }
            ) {
                Text(text = "Upload Image")
            }
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        FirebaseStorageTheme {
            UploadImage()
        }
    }

}



