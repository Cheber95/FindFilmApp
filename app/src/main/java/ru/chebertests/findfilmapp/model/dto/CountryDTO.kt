package ru.chebertests.findfilmapp.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryDTO(
    val iso_3161_1: String?,
    val name: String?
) : Parcelable