package ru.chebertests.findfilmapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Film(
    val id: Int,
    val name: String,
    val posterPath: String,
    val overview: String,
    val year: Int,
    val country: String,
    val genreIds: List<Int>
) : Parcelable