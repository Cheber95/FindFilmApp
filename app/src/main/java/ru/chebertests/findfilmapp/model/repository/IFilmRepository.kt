package ru.chebertests.findfilmapp.model.repository

import retrofit2.Callback
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO
import ru.chebertests.findfilmapp.model.dto.FilmsDTO
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.model.dto.GenresDTO

interface IFilmRepository {
    fun getData(genres: String?, callback: Callback<FilmsDTO>)
    fun getFilm(filmID: Int, callback: Callback<FilmDetailDTO>)
    fun getGenres(callback: Callback<GenresDTO>)
}