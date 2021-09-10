package ru.chebertests.findfilmapp.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GenresDTO(
    val genres: MutableList<GenreDTO>?
) : Parcelable
