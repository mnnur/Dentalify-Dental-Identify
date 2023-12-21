package com.ps108.dentify.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profile(
    val id: String,
    val name: String,
    val email: String,
    val imageUrl: String,
) : Parcelable