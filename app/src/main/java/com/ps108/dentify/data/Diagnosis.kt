package com.ps108.dentify.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Diagnosis(
    val id: String,
    val email: String,
    val diagnosis: String,
    val imageUrl: String,
    val date: String
) : Parcelable
