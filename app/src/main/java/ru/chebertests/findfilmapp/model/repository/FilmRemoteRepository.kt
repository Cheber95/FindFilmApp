package ru.chebertests.findfilmapp.model.repository

import retrofit2.Callback
import ru.chebertests.findfilmapp.model.dto.*
import ru.chebertests.findfilmapp.model.remoteDataSources.RemoteFilmsSource

class FilmRemoteRepository(private val remoteFilmsSource: RemoteFilmsSource) : IFilmRepository {

    override fun getFilmsList(
        genres: String?,
        isAdult: Boolean,
        page: Int?,
        callback: Callback<FilmsDTO>
    ) {
        remoteFilmsSource.getFilmsList(genres, isAdult, page, callback)
    }

    override fun getFilm(filmID: Int, callback: Callback<FilmDetailDTO>) {
        remoteFilmsSource.getFilmDetail(filmID, callback)
    }

    override fun getGenres(callback: Callback<GenresDTO>) {
        remoteFilmsSource.getGenreList(callback)
    }
}
