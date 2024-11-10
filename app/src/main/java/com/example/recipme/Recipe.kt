package com.example.recipme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String? = null,
    val image: String? = null,
    val name: String? = null,
    val cookingtime: String? = null,
    val ingredients: String? = null,
    val preparations: String? = null,

    @field:JvmField
    val isFavorite: Boolean = false
) : Parcelable