package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.model.Film

interface IFilmRepository {
    fun getData(): List<Film>
}