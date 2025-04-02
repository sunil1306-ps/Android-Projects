package com.example.superheroes.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.superheroes.R

data class Superhero(
    @StringRes val nameResourceId: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageResourceId: Int,
)

val superheroes = listOf(
    Superhero(R.string.hero1, R.string.description1, R.drawable.android_superhero1),
    Superhero(R.string.hero2, R.string.description2, R.drawable.android_superhero2),
    Superhero(R.string.hero3, R.string.description3, R.drawable.android_superhero3),
    Superhero(R.string.hero4, R.string.description4, R.drawable.android_superhero4),
    Superhero(R.string.hero5, R.string.description5, R.drawable.android_superhero5),
    Superhero(R.string.hero6, R.string.description6, R.drawable.android_superhero6),
)