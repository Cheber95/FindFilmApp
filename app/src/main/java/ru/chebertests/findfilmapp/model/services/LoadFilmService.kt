package ru.chebertests.findfilmapp.model.services

import android.app.IntentService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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

private const val LOAD_INTENT_FILTER = "LOAD INTENT FILTER"
private const val FILM_ID = "Film ID"
private const val GENRE_ID = "Genre ID"
private const val FILM_LOAD_SUCCESS_EXTRA = "FILM LOAD SUCCESS EXTRA"
private const val GENRES_LOAD_SUCCESS_EXTRA = "GENRES LOAD SUCCESS EXTRA"
private const val LIST_FILM_LOAD_SUCCESS_EXTRA = "LIST OF FILM LOAD SUCCESS EXTRA"
private const val LOAD_RESULT_EXTRA = "LOAD RESULT"
private const val LOAD_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
private const val LOAD_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
private const val LOAD_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
private const val LOAD_URL_MALFORMED_EXTRA = "URL MALFORMED"

private const val COMMAND = "COMMAND"
private const val TO_LOAD_FILM = "TO LOAD FILM"
private const val TO_LOAD_GENRES = "TO LOAD GENRES"
private const val TO_LOAD_LIST_OF_FILMS = "TO LOAD LIST OF FILMS"

private const val LANG_RUS = "ru-RU"

class LoadFilmService(name: String = "LoadService") : IntentService(name) {

    private val broadcastIntent = Intent(LOAD_INTENT_FILTER)
    private val filmRepository: MutableList<Film> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            onEmptyIntent()
        } else {

            when (intent.getStringExtra(COMMAND)) {
                TO_LOAD_FILM -> {
                    val filmID = intent.getIntExtra(FILM_ID, 0)
                    if (filmID == 0) {
                        onEmptyData()
                    } else {
                        loadFilm(filmID)
                    }
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadFilm(id: Int) {
        try {
            val url: String = "https://api.themoviedb.org/3/movie/$id" +
                    "?api_key=${BuildConfig.TMDB_API_KEY}" +
                    "&language=$LANG_RUS"

            val uriFilms = URL(url)

            lateinit var urlConnection: HttpsURLConnection
            try {
                urlConnection = uriFilms.openConnection() as HttpsURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.readTimeout = 10000
                val bufferedReaderFilms =
                    BufferedReader(InputStreamReader(urlConnection.inputStream))
                val filmDetailDTO = Gson().fromJson<FilmDetailDTO>(
                    getLines(bufferedReaderFilms),
                    FilmDetailDTO::class.java
                )

                onResponseOnFilm(filmDetailDTO)

            } catch (e: Exception) {
                onErrorRequest(e.message ?: "Empty error")
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: MalformedURLException) {
            onMalformedURL()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

    private fun onResponseOnFilm(filmDetailDTO: FilmDetailDTO?) {
        onSuccessResponseFilm(filmDetailDTO!!)
    }

    private fun onSuccessResponseFilm(filmDetailDTO: FilmDetailDTO) {
        putLoadResult(FILM_LOAD_SUCCESS_EXTRA)
        broadcastIntent.putExtra(FILM_LOAD_SUCCESS_EXTRA, filmDetailDTO)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onMalformedURL() {
        putLoadResult(LOAD_URL_MALFORMED_EXTRA)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onErrorRequest(s: String) {
        putLoadResult(LOAD_REQUEST_ERROR_EXTRA)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onEmptyData() {
        putLoadResult(LOAD_DATA_EMPTY_EXTRA)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onEmptyIntent() {
        putLoadResult(LOAD_INTENT_EMPTY_EXTRA)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun putLoadResult(resultMessage: String) {
        broadcastIntent.putExtra(LOAD_RESULT_EXTRA, resultMessage)
    }
}