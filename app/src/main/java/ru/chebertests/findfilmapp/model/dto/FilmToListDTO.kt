package ru.chebertests.findfilmapp.model.dto

data class FilmToListDTO(
    val id: Int?,
    val title: String?,
    val poster_path: String?,
    val vote_average: Double?,
    val release_date: String?
)