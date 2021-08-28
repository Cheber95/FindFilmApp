package ru.chebertests.findfilmapp.extensions

import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.FilmDetail
import ru.chebertests.findfilmapp.model.dto.GenreDTO

sealed class AppState {
    data class Success(val listFilms: List<Film>) : AppState()
    data class SuccessOnFilm(val filmDetail: FilmDetail) : AppState()
    data class SuccessOnGenres(val genres: List<GenreDTO>) : AppState()
    class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}
