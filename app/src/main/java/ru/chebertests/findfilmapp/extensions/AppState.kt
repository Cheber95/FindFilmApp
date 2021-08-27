package ru.chebertests.findfilmapp.extensions

import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.FilmDetail
import ru.chebertests.findfilmapp.model.dto.FilmsDTO

sealed class AppState {
    data class Success(val listFilms: List<Film>) : AppState()
    data class SuccessOnFilm(val filmDetail: FilmDetail) : AppState()
    class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}
