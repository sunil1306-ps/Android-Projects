package com.example.scrollview.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.scrollview.R

val RobotSlab = FontFamily(
    Font(R.font.robotoslab_regular)
)

val MontSerrat = FontFamily(
    Font(R.font.robotoslab_regular)
)


// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h1 = TextStyle(
        fontFamily = RobotSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp
    ),
    h2 = TextStyle(
        fontFamily = MontSerrat,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)