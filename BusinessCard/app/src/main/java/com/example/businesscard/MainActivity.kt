package com.example.businesscard

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.businesscard.ui.theme.BusinessCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BusinessCardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(name = "Sunil", title = "Student")
                }
            }
        }
    }
}

@Composable
fun Details(name: String, title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        val image = painterResource(R.drawable.android_logo_2019__stacked__svg__1_)
        Image(
            painter = image,
            contentDescription = null,
            Modifier.size(100.dp)
                .padding(bottom = 10.dp)
        )
        Text(
            text = name,
            fontSize = 50.sp,
            color = Color.White

        )
        Text(
            text = title,
            color = Color.White
        )
    }
}

@Composable
fun IconList(image: Int,text: String, modifier: Modifier = Modifier) {
    Row(
        Modifier.fillMaxWidth()
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(image),
            contentDescription = null,
            Modifier.height(20.dp)
                .padding(end = 20.dp)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            color = Color.White
        )

    }
}
@Composable
fun ContactInfo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 60.dp, end = 60.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        //Icons and text
        IconList(
            R.drawable.kisspng_computer_icons_kariboo_terracina_bike_rental_telep_call_icon_5acc2679890173_4547841715233286335612,
            "7671089848"
        )
        IconList(
            R.drawable.kisspng_computer_icons_share_icon_sharing_symbol_share_5ac0b95ead0136_1345689815225798067086,
            "@insta"
        )
        IconList(
            R.drawable.kisspng_computer_icons_logo_5aead4cf068230_1198150215253393430267,
            "itsmesunil13@gmail.com"
        )

    }
}

@Composable
fun MainScreen(
    name: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color.DarkGray)
    ) {
        Details(name, title, modifier = Modifier.weight(4f))
        ContactInfo(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BusinessCardTheme {
        MainScreen(name = "Sunil", title = "Student")
    }
}