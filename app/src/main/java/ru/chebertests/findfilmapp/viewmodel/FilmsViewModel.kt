package ru.chebertests.findfilmapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.FilmsDTO
import ru.chebertests.findfilmapp.model.remoteDataSources.RemoteFilmsSource
import ru.chebertests.findfilmapp.model.repository.FilmRemoteRepository
import java.io.IOException

private const val SERVER_ERROR = "Ошибка загрузки данных"

class FilmsViewModel(
    private val filmsLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val filmRemoteRepository: FilmRemoteRepository =
        FilmRemoteRepository(RemoteFilmsSource())
) : ViewModel() {

    fun getLiveData() = filmsLiveData

    fun getListFilmFromRemote(requestLink: String) {
        filmsLiveData.value = AppState.Loading
        filmRemoteRepository.getData(requestLink, callback)
    }

    private val callback = object : Callback {

        override fun onResponse(call: Call, response: Response) {
            //val serverResponse: String? = response.message()
            val serverResponse: String? = response.body()?.toString()
            filmsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    chekResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call, e: IOException) {
            filmsLiveData.postValue(AppState.Error(Throwable(SERVER_ERROR)))
        }

    }

    private fun chekResponse(serverResponse: String): AppState {
        val filmsDTO: FilmsDTO =
            Gson().fromJson(serverResponse, FilmsDTO::class.java)

        return if (filmsDTO.results != null) {
            AppState.Success(convertFilmsFromDTO(filmsDTO))
        } else {
            AppState.Error(Throwable(SERVER_ERROR))
        }
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
                        release_date!!.toInt()
                    )
                )
            }
        }
        return listFilms
    }
}
