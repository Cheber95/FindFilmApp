package ru.chebertests.findfilmapp.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GenreDTO(
    val id: Int?,
    val name: String?
) : Parcelable
