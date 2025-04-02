package com.example.firebase

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.example.firebase.ui.theme.FireBaseTheme
import com.example.firebase.uriData.UriData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.skydoves.landscapist.glide.GlideImage

class MainActivity : ComponentActivity() {

    var storage = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FireBaseTheme {
                PickImage()
            }
        }
    }

        var storageRef = storage.reference
    @Composable
    fun ImageContainer(bitmap: ImageBitmap, modifier: Modifier = Modifier) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier
                .height(300.dp)
                .width(200.dp),
            contentScale = ContentScale.FillBounds
        )
    }

    @Composable
    fun PickImage(modifier: Modifier = Modifier) {
        var imageUriState by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current
        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri: Uri? ->
            imageUriState = uri
        }
        Column(
            modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            fun upload() {
                val file = imageUriState
                val riversRef = storageRef.child("images/${file?.lastPathSegment}")
                val uploadTask: UploadTask? = file?.let { riversRef.putFile(it) }
                var gsReference = storage.getReferenceFromUrl("gs://fir-eb54a.appspot.com/images/image:31")

// Register observers to listen for when the download is done or if it fails
                uploadTask?.addOnFailureListener {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_LONG).show()
                }?.addOnSuccessListener {
                    //uriData.UriData.downloadUri = uploadTask.result
                    //UriData.downloadurl = riversRef.downloadUrl
                    Toast.makeText(context,"${gsReference.downloadUrl}", Toast.LENGTH_LONG).show()
                }
            }

            if(imageUriState != null) {
                imageUriState?.let {
                    if(Build.VERSION.SDK_INT < 28) {
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    }else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

                    bitmap.value?.let {btm ->
                        ImageContainer(
                            bitmap = btm.asImageBitmap()
                        )
                    }
                }
            }else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_image_24),
                    contentDescription = null,
                    modifier = modifier
                        .height(300.dp)
                        .width(200.dp)
                        .border(2.dp, Color.White),
                    contentScale = ContentScale.FillBounds
                )
            }

            Spacer(modifier = modifier.height(20.dp))

            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { launcher.launch("image/*") }
                ) {
                    Text(text = "Pick Image")
                }
                Button(onClick = {
                    upload()
                }) {
                    Text(text = "Upload Image")
                }
            }
            Button(onClick = {  }) {
                Text(text = "Download")
            }

            Spacer(modifier = modifier.height(20.dp))
            DownImage()
        }
    }
@Composable
fun DownImage(modifier: Modifier = Modifier) {
    GlideImage(
        imageModel = { UriData.downloadurl },
        requestBuilder = {
            Glide
                .with(LocalContext.current)
                .asBitmap()
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .thumbnail(0.6f)
                .transition(withCrossFade())
        },
        modifier = modifier,
        loading = {
            Box(modifier = Modifier.matchParentSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        failure = {
            Text(text = "image request failed.")
        }
    )
}


    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        FireBaseTheme {
            PickImage()
        }
    }

}