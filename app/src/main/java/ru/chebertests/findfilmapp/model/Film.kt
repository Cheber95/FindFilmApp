package ru.chebertests.findfilmapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Film(
    val id: Int,
    val name: String,
    val posterPath: String,
    val voteAverage: Double,
    val year: Int
) : Parcelable