package com.example.affirmation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Affirmation {
    @StringRes val stringResourceId: Int = 0,
    @DrawableRes val imageResourceId: Int = 0
}