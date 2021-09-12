package ru.chebertests.findfilmapp.viewmodel

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.chebertests.findfilmapp.BuildConfig
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.FilmDetail
import ru.chebertests.findfilmapp.model.dto.*
import ru.chebertests.findfilmapp.model.remoteDataSources.RemoteFilmsSource
import ru.chebertests.findfilmapp.model.repository.FilmRemoteRepository
import ru.chebertests.findfilmapp.model.repository.LocalRepository
import ru.chebertests.findfilmapp.model.repository.LocalRepositoryImpl
import java.time.LocalDate
import ru.chebertests.findfilmapp.app.App.Companion.getHistoryDao

private const val SERVER_ERROR = "Ошибка загрузки данных"
private const val IMG_NOT_FOUND =
    "https://icon-library.com/images/no-image-icon/no-image-icon-26.jpg"

class FilmsViewModel(
    private val filmsLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val filmRemoteRepository: FilmRemoteRepository =
        FilmRemoteRepository(RemoteFilmsSource()),
    private val filmLocalRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao())
) : ViewModel() {

    private var numberOfPages: Int = 0

    fun getNumberOfPages() = numberOfPages

    fun getLiveData() = filmsLiveData

    fun getListFilmFromRemote(genres: String?, page: Int, isAdult: Boolean) {
        filmsLiveData.value = AppState.Loading
        filmRemoteRepository.getFilmsList(genres, isAdult, page, callbackList)
    }

    fun getFilmDetailFromRemote(film: Film) {
        filmsLiveData.value = AppState.Loading
        filmRemoteRepository.getFilm(film.id, callbackFilm)
    }

    fun getGenresListFromRemote() {
        filmsLiveData.value = AppState.Loading
        filmRemoteRepository.getGenres(callbackListGenres)
    }

    fun saveFilmToDB(filmDetail: FilmDetail) {
        Thread {
            filmLocalRepository.saveEntity(filmDetail)
        }.start()
    }

    fun updateFilmOnDB(filmDetail: FilmDetail) {
        Thread {
            filmLocalRepository.updateEntity(filmDetail)
        }.start()
    }

    private val callbackList = object : Callback<FilmsDTO> {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onResponse(call: Call<FilmsDTO>, response: Response<FilmsDTO>) {
            val serverResponse: FilmsDTO? = response.body()
            filmsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    val requestURL = response.toString().substringAfter("url=").replace("}", "")
                    chekResponseList(serverResponse, requestURL)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<FilmsDTO>, t: Throwable) {
            filmsLiveData.postValue(AppState.Error(Throwable(SERVER_ERROR)))
        }

    }

    private val callbackFilm = object : Callback<FilmDetailDTO> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onResponse(call: Call<FilmDetailDTO>, response: Response<FilmDetailDTO>) {
            val serverResponse: FilmDetailDTO? = response.body()
            filmsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    chekResponseFilm(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<FilmDetailDTO>, t: Throwable) {
            filmsLiveData.postValue(AppState.Error(Throwable(SERVER_ERROR)))
        }

    }

    private val callbackListGenres = object : Callback<GenresDTO> {
        override fun onResponse(call: Call<GenresDTO>, response: Response<GenresDTO>) {
            val serverResponse: GenresDTO? = response.body()
            filmsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    chekResponseGenres(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<GenresDTO>, t: Throwable) {
            filmsLiveData.postValue(AppState.Error(Throwable(SERVER_ERROR)))
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chekResponseList(serverResponse: FilmsDTO, requestURL: String): AppState =
        if (serverResponse.results != null) {
            if (requestURL.contains("with_genres")) {
                val genreID = requestURL.substringAfter("with_genres=").substringBefore("&").toInt()
                AppState.SuccessOnListByGenre(convertFilmsFromDTO(serverResponse), genreID)
            } else {
                AppState.SuccessOnList(convertFilmsFromDTO(serverResponse))
            }
        } else {
            AppState.Error(Throwable(SERVER_ERROR))
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chekResponseFilm(serverResponse: FilmDetailDTO): AppState =
        if (serverResponse.id != null) {
            AppState.SuccessOnFilm(convertFilmDetailFromFilmDetailDTO(serverResponse))
        } else {
            AppState.Error(Throwable(SERVER_ERROR))
        }


    private fun chekResponseGenres(serverResponse: GenresDTO): AppState =
        if (serverResponse.genres != null) {
            AppState.SuccessOnGenres(serverResponse.genres)
        } else {
            AppState.Error(Throwable(SERVER_ERROR))
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertFilmsFromDTO(filmsDTO: FilmsDTO): List<Film> {
        val listFilms = mutableListOf<Film>()
        filmsDTO.total_pages?.let { numberOfPages = it }
        for (film in filmsDTO.results!!) {
            var year: Int
            try {
                year = LocalDate.parse(film.release_date).year
            } catch (e: Exception) {
                Log.e(e.toString(), film.title + film.id.toString())
                year = 0
            }

            with(film) {
                val filmID: Int = id ?: 0
                val filmTitle: String = title ?: ""
                val filmPoster: String = if (poster_path != null) {
                    "https://image.tmdb.org/t/p/original/${poster_path}"
                } else {
                    IMG_NOT_FOUND
                }
                val filmVoteAverage: Double = vote_average ?: 0.0

                listFilms.add(
                    Film(
                        filmID,
                        filmTitle,
                        filmPoster,
                        filmVoteAverage,
                        year
                    )
                )
            }
        }
        return listFilms
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertFilmDetailFromFilmDetailDTO(filmDetailDTO: FilmDetailDTO): FilmDetail {
        val date = try {
            LocalDate.parse(filmDetailDTO.release_date)
        } catch (e: Exception) {
            Log.e(e.toString(), filmDetailDTO.title + filmDetailDTO.id.toString())
            LocalDate.of(0, 1, 1)
        }

        val id: Int = filmDetailDTO.id ?: 0
        val title: String = filmDetailDTO.title ?: ""
        val poster: String = if (filmDetailDTO.poster_path != null) {
            "https://image.tmdb.org/t/p/original/${filmDetailDTO.poster_path}"
        } else {
            IMG_NOT_FOUND
        }
        val voteAverage: Double = filmDetailDTO.vote_average ?: 0.0
        val budget: Int = filmDetailDTO.budget ?: 0
        val genres: String = if (filmDetailDTO.genres != null) {
            genresToString(filmDetailDTO.genres)
        } else {
            ""
        }
        val overview = filmDetailDTO.overview ?: ""
        val countries: String = if (filmDetailDTO.production_countries != null) {
            countriesToString(filmDetailDTO.production_countries)
        } else {
            ""
        }

        return FilmDetail(
            id,
            title,
            poster,
            voteAverage,
            date,
            budget,
            genres,
            overview,
            countries
        )
    }

    private fun genresToString(genres: List<GenreDTO>): String {
        val genresString = mutableListOf<String>()
        for (genre in genres) {
            genre.name?.let { genresString.add(it) }
        }
        return genresString
            .toString()
            .replace("[", "", true)
            .replace("]", "", true)
    }

    private fun countriesToString(countries: List<CountryDTO>): String {
        val countriesString = mutableListOf<String>()
        for (country in countries) {
            country.name?.let { countriesString.add(it) }
        }
        return countriesString
            .toString()
            .replace("[", "", true)
            .replace("]", "", true)
    }
}
