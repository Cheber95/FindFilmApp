package ru.chebertests.findfilmapp.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilmsDTO(
    val results: List<FilmToListDTO>?,
    val total_pages: Int?
) : Parcelable
