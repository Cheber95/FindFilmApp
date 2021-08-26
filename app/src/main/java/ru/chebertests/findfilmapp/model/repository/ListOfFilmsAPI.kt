package ru.chebertests.findfilmapp.model.repository

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.chebertests.findfilmapp.model.dto.FilmsDTO

private const val FIELD_API_KEY = "api_key"
private const val FIELD_LANG = "language"
private const val ENDPOINT = "discover/movie"

interface ListOfFilmsAPI {
    @GET(ENDPOINT)
    fun getFilms(
        @Query(FIELD_API_KEY) apikey: String,
        @Query(FIELD_LANG) lang: String
    ) : Call<FilmsDTO>
}