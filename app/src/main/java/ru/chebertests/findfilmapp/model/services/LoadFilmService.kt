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

const val LOAD_INTENT_FILTER = "LOAD INTENT FILTER"
const val FILM_ID = "Film ID"
const val GENRE_ID = "Genre ID"
const val FILM_LOAD_SUCCESS_EXTRA = "FILM LOAD SUCCESS EXTRA"
const val GENRES_LOAD_SUCCESS_EXTRA = "GENRES LOAD SUCCESS EXTRA"
const val LIST_FILM_LOAD_SUCCESS_EXTRA = "LIST OF FILM LOAD SUCCESS EXTRA"
const val LOAD_RESULT_EXTRA = "LOAD RESULT"
const val LOAD_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val LOAD_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val LOAD_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val LOAD_URL_MALFORMED_EXTRA = "URL MALFORMED"

const val COMMAND = "COMMAND"
const val TO_LOAD_FILM = "TO LOAD FILM"
const val TO_LOAD_GENRES = "TO LOAD GENRES"
const val TO_LOAD_LIST_OF_FILMS = "TO LOAD LIST OF FILMS"

const val LANG_RUS = "ru-RU"

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
                TO_LOAD_GENRES -> {
                    loadGenres()
                }
                TO_LOAD_LIST_OF_FILMS -> {
                    val filmGenreID = intent.getIntExtra(GENRE_ID, 0)
                    if (filmGenreID == 0) {
                        loadListOfFilm(null)
                    } else {
                        loadListOfFilm(filmGenreID)
                    }
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadListOfFilm(genreID: Int?) {
        val page = 1
        try {
            val urlBase: String = "https://api.themoviedb.org/3/discover/movie" +
                    "?api_key=${BuildConfig.TMDB_API_KEY}" +
                    "&language=$LANG_RUS" +
                    "&page=$page"
            var urlGenre = ""
            if (genreID != null) {
                urlGenre = "&with_genres=${genreID.toString()}"
            }

            val uriFilms = URL(urlBase + urlGenre)

            lateinit var urlConnection: HttpsURLConnection
            try {
                urlConnection = uriFilms.openConnection() as HttpsURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.readTimeout = 10000
                val bufferedReaderGenres =
                    BufferedReader(InputStreamReader(urlConnection.inputStream))
                val filmsDTO = Gson().fromJson(
                    getLines(bufferedReaderGenres),
                    FilmsDTO::class.java
                )
                urlConnection.disconnect()

                filmsParser(filmsDTO)

                onResponseOnListOfFilm(filmRepository)

            } catch (e: Exception) {
                onErrorRequest(e.message ?: "Empty error")
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: MalformedURLException) {
            onMalformedURL()
        }
    }

    private fun onResponseOnListOfFilm(filmRepository: List<Film>?) {
        onSuccessResponseListFilms(filmRepository!!)
    }

    private fun onSuccessResponseListFilms(filmRepository: List<Film>) {
        putLoadResult(LIST_FILM_LOAD_SUCCESS_EXTRA)
        broadcastIntent.putExtra(LIST_FILM_LOAD_SUCCESS_EXTRA, filmRepository.toTypedArray())
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadGenres() {
        try {
            val uriGenres =
                URL(
                    "https://api.themoviedb.org/3/genre/movie/list" +
                            "?api_key=${BuildConfig.TMDB_API_KEY}" +
                            "&language=$LANG_RUS"
                )

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

                onResponseOnGenres(genresDTO.genres)

            } catch (e: Exception) {
                onErrorRequest(e.message ?: "Empty error")
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: MalformedURLException) {
            onMalformedURL()
        }
    }

    private fun onResponseOnGenres(genres: List<GenreDTO>?) {
        onSuccessResponseGenres(genres!!)
    }

    private fun onSuccessResponseGenres(genres: List<GenreDTO>) {
        putLoadResult(GENRES_LOAD_SUCCESS_EXTRA)
        broadcastIntent.putExtra(GENRES_LOAD_SUCCESS_EXTRA, genres.toTypedArray())
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
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

    private fun filmsParser(filmsFromAPI: FilmsDTO) {

        for (filmToListDTO: FilmToListDTO in filmsFromAPI.results) {

            val currentFilm = Film(
                filmToListDTO.id!!,
                filmToListDTO.title!!,
                "https://image.tmdb.org/t/p/original/" + filmToListDTO.poster_path!!,
                filmToListDTO.vote_average!!,
                releaseDateParser(filmToListDTO.release_date!!),
            )
            filmRepository.add(currentFilm)
        }
    }

    private fun releaseDateParser(dateOnString: String): Int {
        return dateOnString.substring(0, 4).toInt()
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