package ru.chebertests.findfilmapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.FilmsDTO
import ru.chebertests.findfilmapp.model.remoteDataSources.RemoteFilmsSource
import ru.chebertests.findfilmapp.model.repository.FilmRemoteRepository
import java.io.IOException
import java.time.LocalDate

private const val SERVER_ERROR = "Ошибка загрузки данных"

class FilmsViewModel(
    private val filmsLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val filmRemoteRepository: FilmRemoteRepository =
        FilmRemoteRepository(RemoteFilmsSource())
) : ViewModel() {

    fun getLiveData() = filmsLiveData

    fun getListFilmFromRemote(genres: String?) {
        filmsLiveData.value = AppState.Loading
        filmRemoteRepository.getData(genres, callback)
    }

    private val callback = object : Callback<FilmsDTO> {


        override fun onFailure(call: Call<FilmsDTO>, t: Throwable) {
            filmsLiveData.postValue(AppState.Error(Throwable(SERVER_ERROR)))
        }

        override fun onResponse(call: Call<FilmsDTO>, response: Response<FilmsDTO>) {
            val serverResponse: FilmsDTO? = response.body()
            filmsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    chekResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

    }

    private fun chekResponse(serverResponse: FilmsDTO): AppState =
        if (serverResponse.results != null) {
            AppState.Success(convertFilmsFromDTO(serverResponse))
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
}
