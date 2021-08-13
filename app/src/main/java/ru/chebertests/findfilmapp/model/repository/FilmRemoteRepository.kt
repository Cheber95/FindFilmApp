package ru.chebertests.findfilmapp.model.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import ru.chebertests.findfilmapp.BuildConfig
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.CountriesDTO
import ru.chebertests.findfilmapp.model.dto.FilmsDTO
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

class FilmRemoteRepository : IFilmRepository {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun getData(): List<Film> {
        var filmRepository : List<Film> = mutableListOf()
        try {
            Thread(Runnable {
                val uriGenres =
                    URL("https://api.themoviedb.org/3/genre/movie/list?api_key=$API_KEY&language=$LANG")
                val uriCountries =
                    URL("https://api.themoviedb.org/3/configuration/countries?api_key=$API_KEY&language=$LANG")
                val uriFilms =
                    URL("https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY&language=$LANG")

                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection = uriGenres.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.addRequestProperty("api_key", API_KEY)
                    urlConnection.readTimeout = 10000
                    val bufferedReaderGenres = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val genresDTO = Gson().fromJson<GenresDTO>(getLines(bufferedReaderGenres), GenresDTO::class.java)
                    urlConnection.disconnect()

                    urlConnection = uriCountries.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.readTimeout = 10000
                    val bufferedReaderCountries = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val countriesDTO = Gson().fromJson<CountriesDTO>(getLines(bufferedReaderCountries), CountriesDTO::class.java)
                    urlConnection.disconnect()

                    urlConnection = uriFilms.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.readTimeout = 10000
                    val bufferedReaderFilms = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val filmsDTO = Gson().fromJson<FilmsDTO>(getLines(bufferedReaderCountries), FilmsDTO::class.java)
                    urlConnection.disconnect()

                    filmRepository = filmsParser(genresDTO, countriesDTO, filmsDTO)
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
        //val filmRepository = filmsParser()
        return filmRepository
    }

    private fun filmsParser(genresDTO: GenresDTO,
                            countriesDTO: CountriesDTO,
                            filmsFromAPI: FilmsDTO?): List<Film> {
        //TODO
        val films = FilmLocalRepository()
        return films.getData()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}
