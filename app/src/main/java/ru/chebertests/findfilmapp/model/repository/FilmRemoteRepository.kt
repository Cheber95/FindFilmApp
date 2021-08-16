package ru.chebertests.findfilmapp.model.repository

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import ru.chebertests.findfilmapp.BuildConfig
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection
import ru.chebertests.findfilmapp.model.Callback

private const val API_KEY = BuildConfig.TMDB_API_KEY
private const val LANG = "ru-RU"

class FilmRemoteRepository : IFilmRepository {

    private val filmRepository: MutableList<Film> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getData(callback: Callback<List<Film>>, genreID: Int?) {
        val handler = Handler(Looper.getMainLooper())
        try {
            Thread(Runnable {

                val urlBase : String = "https://api.themoviedb.org/3/discover/movie" +
                "?api_key=$API_KEY" +
                "&language=$LANG" +
                "&sort_by=vote_count.desc"
                var urlGenre : String = ""
                if (genreID != null) {
                    urlGenre = "&with_genres=${genreID.toString()}"
                }

                val uriFilms = URL(urlBase + urlGenre)

                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection = uriFilms.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.readTimeout = 10000
                    val bufferedReaderFilms =
                        BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val filmsDTO = Gson().fromJson<FilmsDTO>(
                        getLines(bufferedReaderFilms),
                        FilmsDTO::class.java
                    )
                    urlConnection.disconnect()

                    filmsParser(filmsDTO)
                    handler.post(Runnable { callback.onSuccess(filmRepository) })

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

    private fun filmsParser(filmsFromAPI: FilmsDTO) {

        for (filmDTO: FilmDTO in filmsFromAPI.results) {
            val currentFilm: Film = Film(
                filmDTO.id!!,
                filmDTO.title!!,
                "https://image.tmdb.org/t/p/original/" + filmDTO.poster_path!!,
                filmDTO.overview!!,
                releaseDateParser(filmDTO.release_date!!),
                "Россия",
                filmDTO.genre_ids!!
            )
            filmRepository.add(currentFilm)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

    private fun releaseDateParser(dateOnString: String): Int {
        return dateOnString.substring(0, 4).toInt()
    }
}
