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
private const val MAX_PAGES_OF_FILMS = 2
private const val LANG = "ru-RU"

class FilmRemoteRepository : IFilmRepository {

    private val filmRepository: MutableList<Film> = mutableListOf()

    override fun setData(dataToSet: List<Film>) {
        filmRepository.clear()
        filmRepository.addAll(dataToSet)
    }

    override fun getData(): List<Film> {
        return filmRepository
    }
}
