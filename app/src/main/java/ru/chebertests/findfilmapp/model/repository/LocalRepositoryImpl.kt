package ru.chebertests.findfilmapp.model.repository

import android.os.Build
import androidx.annotation.RequiresApi
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.FilmDetail
import ru.chebertests.findfilmapp.model.room.FilmDAO
import ru.chebertests.findfilmapp.model.room.FilmEntity
import java.time.LocalDate
import java.time.temporal.ChronoField

class LocalRepositoryImpl(private val localDataSource: FilmDAO) : LocalRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllHistory(): List<FilmDetail> {
        return convertFilmEntityToFilm(localDataSource.all())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun saveEntity(filmDetail: FilmDetail) {
        localDataSource.insert(convertFilmToFilmEntity(filmDetail))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertFilmToFilmEntity(filmDetail: FilmDetail): FilmEntity {
        with(filmDetail) {
            return FilmEntity(
                id.toLong(),
                name,
                posterPath,
                voteAverage,
                releaseDate.getLong(ChronoField.EPOCH_DAY),
                budget,
                genres,
                overview,
                countries
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertFilmEntityToFilm(list: List<FilmEntity>): List<FilmDetail> {
        return list.map {
            FilmDetail(
                it.id.toInt(),
                it.name,
                it.posterPath,
                it.voteAverage,
                LocalDate.ofEpochDay(it.releaseDate),
                it.budget,
                it.genres,
                it.overview,
                it.countries
            )
        }
    }
}