package ru.chebertests.findfilmapp.model.dto

data class FilmsDTO(
    val results: List<FilmToListDTO>,
    val total_pages: Int
)
