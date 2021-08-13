package ru.chebertests.findfilmapp.model.dto

data class FilmDTO(
    val id: Int?,
    val title: String?,
    val poster_path: String?,
    val overview: String?,
    val release_date: String?,
    val production_countries: CountriesDTO?,
    val genres: GenresDTO?

)