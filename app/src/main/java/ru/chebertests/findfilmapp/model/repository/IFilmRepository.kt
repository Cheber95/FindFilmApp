package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO

interface IFilmRepository {
    fun getData(callback : Callback<List<Film>>, genreID: Int?)
    fun getFilm(callback : Callback<FilmDetailDTO>, filmID: Int)
}