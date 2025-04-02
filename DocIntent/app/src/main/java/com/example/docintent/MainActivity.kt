package com.example.docintent


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.example.docintent.ui.theme.DocIntentTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path1 = "https://firebasestorage.googleapis.com/v0/b/docs-view.appspot.com/o/fee1.pdf?alt=media&token=a9c6b511-f9ab-49fc-88bb-f20bf5af206c"
        val path = path1.toUri()
        setContent {
            DocIntentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalContext provides this@MainActivity) {
                        AppMain(this)
                    }
                }
            }


            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )
            }
        }

    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Greeting(onclick: () -> Unit) {
//
//    var text by rememberSaveable { mutableStateOf("") }
//    Column {
//        OutlinedTextField(
//            value = text,
//            onValueChange = { text = it },
//            label = { Text("Name") },
//            placeholder = { Text("your name...") }
//        )
//        Button(onClick = { onclick() }) {
//            Text(text = "call")
//        }
//    }
//}
//
//@Composable
//fun Docintent(
//    onclick: () -> Unit
//) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(
//            onClick = {
//                onclick()
//            }
//        ) {
//            Text(text = "Click Me")
//        }
//
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallDialerApp(context: Context) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Call Dialer",
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
            },
            label = { Text(text = "Enter Phone Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Button(
            onClick = {
                if (phoneNumber.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:$phoneNumber")
                    startActivity(context, intent, null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Call")
        }
    }
}

@Composable
fun AppMain(context: Context) {
    CallDialerApp(context)
}