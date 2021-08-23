package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.dto.GenreDTO

interface IGenresRepository {
    fun getGenres(callback: Callback<List<GenreDTO>>)
}