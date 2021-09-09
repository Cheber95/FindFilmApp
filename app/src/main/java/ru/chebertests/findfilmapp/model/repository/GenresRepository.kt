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

    private var genresRepository: MutableList<GenreDTO> = mutableListOf()

    fun setGenres(genresToSet: List<GenreDTO>) {
        genresRepository.clear()
        genresRepository.addAll(genresToSet)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getGenres(callback: Callback<List<GenreDTO>>) {
        callback.onSuccess(genresRepository)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}