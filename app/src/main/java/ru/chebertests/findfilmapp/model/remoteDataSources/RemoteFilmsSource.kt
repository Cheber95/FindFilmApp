package ru.chebertests.findfilmapp.model.remoteDataSources

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import ru.chebertests.findfilmapp.BuildConfig

private const val API_KEY = "api_key"

class RemoteFilmsSource {
    fun getFilmsList(requestLink: String, callback: Callback) {
        val builder: Request.Builder = Request.Builder().apply {
            header(API_KEY, BuildConfig.TMDB_API_KEY)
            url(requestLink)
        }
        val request = builder.build()
        val call = OkHttpClient().newCall(request)
        call.enqueue(callback)
    }
}