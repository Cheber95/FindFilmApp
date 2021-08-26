package ru.chebertests.findfilmapp.model.remoteDataSources

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import ru.chebertests.findfilmapp.BuildConfig

private const val API_KEY = "api_key"

class RemoteFilmsSource {
    fun getFilmsList(requestLink: String, callback: Callback) {
        val builder: Request.Builder = Request.Builder().apply {
            //header(API_KEY, BuildConfig.TMDB_API_KEY)
            //url(requestLink)
            url("https://api.themoviedb.org/3/discover/movie?api_key=85a6977294202ae5da2d96ff6d2ed326&language=ru-RU")
        }
        OkHttpClient().newCall(builder.build()).enqueue(callback)
    }
}