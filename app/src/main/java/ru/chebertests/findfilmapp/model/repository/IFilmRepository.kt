package ru.chebertests.findfilmapp.model.repository

import okhttp3.Callback
import ru.chebertests.findfilmapp.model.dto.FilmsDTO

interface IFilmRepository {
    fun getData(requestLink: String, callback: Callback)
}