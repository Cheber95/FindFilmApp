package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.model.FilmDetail

interface LocalRepository {
    fun getAllHistory(): List<FilmDetail>
    fun clearHistory()
    fun updateEntity(filmDetail: FilmDetail)
    fun saveEntity(filmDetail: FilmDetail)
}