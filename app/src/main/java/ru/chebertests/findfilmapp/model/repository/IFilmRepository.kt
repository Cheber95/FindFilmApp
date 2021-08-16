package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.Film

interface IFilmRepository {
    fun getData(callback : Callback<List<Film>>, genreID: Int?)
}