package ru.chebertests.findfilmapp.model.repository

import android.os.Build
import androidx.annotation.RequiresApi
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

    override fun clearHistory() {
        localDataSource.deleteAll()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun updateEntity(filmDetail: FilmDetail) {
        localDataSource.update(convertFilmToFilmEntity(filmDetail))
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
                countries,
                getNote(),
                getTimeLong()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertFilmEntityToFilm(list: List<FilmEntity>): List<FilmDetail> {
        var listFilms: MutableList<FilmDetail> = mutableListOf()
        for (entity in list) {
            val film = FilmDetail(
                entity.id.toInt(),
                entity.name,
                entity.posterPath,
                entity.voteAverage,
                LocalDate.ofEpochDay(entity.releaseDate),
                entity.budget,
                entity.genres,
                entity.overview,
                entity.countries
            )
            film.setTimeLong(entity.time)
            film.setNote(entity.note)
            listFilms.add(film)
        }
        return listFilms
    }
}