package ru.chebertests.findfilmapp.model.repository.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO
import ru.chebertests.findfilmapp.model.dto.FilmsDTO

private const val FIELD_API_KEY = "api_key"
private const val FIELD_LANG = "language"
private const val FIELD_GENRES = "with_genres"
private const val ENDPOINT = "discover/movie"

interface ListOfFilmsAPI {

    @GET(ENDPOINT)
    fun getFilms(
        @Query(FIELD_API_KEY) apikey: String,
        @Query(FIELD_LANG) lang: String,
        @Query(FIELD_GENRES) genres: String?
    ) : Call<FilmsDTO>

    @GET("movie/{id}")
    fun getFilm(
        @Path("id") id: Int,
        @Query(FIELD_API_KEY) apikey: String,
        @Query(FIELD_LANG) lang: String,
    ) : Call<FilmDetailDTO>
}