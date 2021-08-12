package ru.chebertests.findfilmapp.model.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import ru.chebertests.findfilmapp.BuildConfig
import ru.chebertests.findfilmapp.model.Film
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

private const val API_KEY = BuildConfig.TMDB_API_KEY

class FilmRemoteRepository : IFilmRepository {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun getData(): List<Film> {
        var res : MutableList<Film> = mutableListOf()
        try {
            val uri =
                URL("https://api.themoviedb.org/3/movie/550?adult=true")
            Thread(Runnable {
                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection = uri.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.addRequestProperty(
                        "api_key",
                        API_KEY
                    )
                    urlConnection.readTimeout = 10000
                    val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    res.add(Gson().fromJson(getLines(bufferedReader), Film::class.java))
                } catch (e: Exception) {
                    Log.e("", "Fail connection", e)
                    e.printStackTrace()
                } finally {
                    urlConnection.disconnect()
                }
            }).start()
        } catch (e: MalformedURLException) {
            Log.e("", "Fail URI", e)
            e.printStackTrace()
        }
        return res
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}