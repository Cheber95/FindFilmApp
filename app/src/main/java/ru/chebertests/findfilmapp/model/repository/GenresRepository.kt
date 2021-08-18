package ru.chebertests.findfilmapp.model.repository

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import ru.chebertests.findfilmapp.BuildConfig
import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.model.dto.GenresDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

private const val API_KEY = BuildConfig.TMDB_API_KEY
private const val LANG = "ru-RU"

class GenresRepository : IGenresRepository {

    private lateinit var genresRepository : List<GenreDTO>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getGenres(callback: Callback<List<GenreDTO>>) {
        val handler = Handler(Looper.getMainLooper())
        try {
            Thread(Runnable {
                val uriGenres =
                    URL("https://api.themoviedb.org/3/genre/movie/list?api_key=$API_KEY&language=$LANG")

                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection = uriGenres.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.readTimeout = 10000
                    val bufferedReaderGenres =
                        BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val genresDTO = Gson().fromJson<GenresDTO>(
                        getLines(bufferedReaderGenres),
                        GenresDTO::class.java
                    )
                    urlConnection.disconnect()

                    genresRepository = genresDTO.genres!!
                    handler.post(Runnable {
                        callback.onSuccess(genresRepository)
                    })

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
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadData() {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}