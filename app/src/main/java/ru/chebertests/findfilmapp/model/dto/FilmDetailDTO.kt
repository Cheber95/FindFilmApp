package ru.chebertests.findfilmapp.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilmDetailDTO(
    val id: Int?,
    val title: String?,
    val poster_path: String?,
    val vote_average: Double?,
    val release_date: String?,
    val budget: Int?,
    val genres: List<GenreDTO>?,
    val overview: String?,
    val production_countries: List<CountryDTO>?
) : Parcelable
