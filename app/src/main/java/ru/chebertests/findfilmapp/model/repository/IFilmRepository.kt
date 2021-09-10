package ru.chebertests.findfilmapp.model.repository

import retrofit2.Callback
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO
import ru.chebertests.findfilmapp.model.dto.FilmsDTO
import ru.chebertests.findfilmapp.model.dto.GenresDTO

interface IFilmRepository {
    fun getFilmsList(genres: String?, isAdult: Boolean, callback: Callback<FilmsDTO>)
    fun getFilm(filmID: Int, callback: Callback<FilmDetailDTO>)
    fun getGenres(callback: Callback<GenresDTO>)
}