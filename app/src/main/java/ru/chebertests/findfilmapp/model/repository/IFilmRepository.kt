package ru.chebertests.findfilmapp.model.repository

import retrofit2.Callback
import ru.chebertests.findfilmapp.model.dto.FilmsDTO

interface IFilmRepository {
    fun getData(callback: Callback<FilmsDTO>)
}