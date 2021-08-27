package ru.chebertests.findfilmapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.FilmDetail
import ru.chebertests.findfilmapp.model.dto.CountryDTO
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO
import ru.chebertests.findfilmapp.model.dto.FilmsDTO
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.model.remoteDataSources.RemoteFilmsSource
import ru.chebertests.findfilmapp.model.repository.FilmRemoteRepository
import java.time.LocalDate
import java.util.*

private const val SERVER_ERROR = "Ошибка загрузки данных"

class FilmsViewModel(
    private val filmsLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val filmRemoteRepository: FilmRemoteRepository =
        FilmRemoteRepository(RemoteFilmsSource())
) : ViewModel() {

    fun getLiveData() = filmsLiveData

    fun getListFilmFromRemote(genres: String?) {
        filmsLiveData.value = AppState.Loading
        filmRemoteRepository.getData(genres, callbackList)
    }

    fun getFilmDetailFromRemote(film: Film) {
        filmsLiveData.value = AppState.Loading
        filmRemoteRepository.getFilm(film.id,callbackFilm)
    }

    private val callbackList = object : Callback<FilmsDTO> {

        override fun onResponse(call: Call<FilmsDTO>, response: Response<FilmsDTO>) {
            val serverResponse: FilmsDTO? = response.body()
            filmsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    chekResponseList(serverResponse)
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
            TODO("Not yet implemented")
        }

    }

    private fun chekResponseList(serverResponse: FilmsDTO): AppState =
        if (serverResponse.results != null) {
            AppState.Success(convertFilmsFromDTO(serverResponse))
        } else {
            AppState.Error(Throwable(SERVER_ERROR))
        }

    private fun chekResponseFilm(serverResponse: FilmDetailDTO): AppState =
        if (serverResponse.id != null) {
            AppState.SuccessOnFilm(convertFilmDetailFromFilmDetailDTO(serverResponse))
        } else {
            AppState.Error(Throwable(SERVER_ERROR))
        }

    private fun convertFilmsFromDTO(filmsDTO: FilmsDTO): List<Film> {
        val listFilms = mutableListOf<Film>()
        for (film in filmsDTO.results!!) {
            with(film) {
                listFilms.add(
                    Film(
                        id!!,
                        title!!,
                        "https://image.tmdb.org/t/p/original/${poster_path!!}",
                        vote_average!!,
                        LocalDate.parse(release_date).year
                    )
                )
            }
        }
        return listFilms
    }

    private fun convertFilmDetailFromFilmDetailDTO(filmDetailDTO: FilmDetailDTO) =
        FilmDetail(
            filmDetailDTO.id!!,
            filmDetailDTO.title!!,
            "https://image.tmdb.org/t/p/original/${filmDetailDTO.poster_path!!}",
            filmDetailDTO.vote_average!!,
            LocalDate.parse(filmDetailDTO.release_date!!),
            filmDetailDTO.budget!!,
            genresToString(filmDetailDTO.genres!!),
            filmDetailDTO.overview!!,
            countriesToString(filmDetailDTO.production_countries!!)
        )

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
