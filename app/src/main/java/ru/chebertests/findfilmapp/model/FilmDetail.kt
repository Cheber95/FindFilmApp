package ru.chebertests.findfilmapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class FilmDetail(
    val id: Int,
    val name: String,
    val posterPath: String,
    val voteAverage: Double,
    val releaseDate: LocalDate,
    val budget: Int,
    val genres: String,
    val overview: String,
    val countries: String
) : Parcelable