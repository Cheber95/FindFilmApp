package ru.chebertests.findfilmapp.model.remoteDataSources

import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.chebertests.findfilmapp.BuildConfig
import ru.chebertests.findfilmapp.model.dto.FilmsDTO
import ru.chebertests.findfilmapp.model.repository.ListOfFilmsAPI

private const val LANG_RUS = "ru-RU"
private const val BASE_URL = "https://api.themoviedb.org/3/"

class RemoteFilmsSource {

    private val listOfFilmsAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build().create(ListOfFilmsAPI::class.java)

    fun getFilmsList(callback: Callback<FilmsDTO>) {
        listOfFilmsAPI.getFilms(BuildConfig.TMDB_API_KEY, LANG_RUS).enqueue(callback)
    }
}