package ru.chebertests.findfilmapp.view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmDetailFragmentBinding
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.CountryDTO
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.model.services.LoadFilmService

const val LOAD_INTENT_FILTER = "LOAD INTENT FILTER"
const val FILM_ID = "Film ID"
const val FILM_LOAD_SUCCESS_EXTRA = "FILM LOAD SUCCESS EXTRA"
private const val LOAD_RESULT_EXTRA = "LOAD RESULT"
private const val LOAD_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
private const val LOAD_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
private const val LOAD_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
private const val LOAD_URL_MALFORMED_EXTRA = "URL MALFORMED"

const val COMMAND = "COMMAND"
const val TO_LOAD_FILM = "TO LOAD FILM"
const val TO_LOAD_GENRES = "TO LOAD GENRES"
const val TO_LOAD_LIST_OF_FILMS = "TO LOAD LIST OF FILMS"

private const val PROCESS_ERROR = "Обработка ошибки"

class FilmDetailFragment : Fragment() {

    private var _binding: FilmDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra(LOAD_RESULT_EXTRA)) {
                LOAD_INTENT_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                LOAD_DATA_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                LOAD_REQUEST_ERROR_EXTRA -> TODO(PROCESS_ERROR)
                LOAD_URL_MALFORMED_EXTRA -> TODO(PROCESS_ERROR)
                FILM_LOAD_SUCCESS_EXTRA -> {
                    intent.getParcelableExtra<FilmDetailDTO>(FILM_LOAD_SUCCESS_EXTRA)?.let {
                        renderFilm(
                            it
                        )
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(LOAD_INTENT_FILTER))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        context?.let {
            it.startService(Intent(it, LoadFilmService::class.java).apply {
                putExtra(COMMAND, TO_LOAD_FILM)
                putExtra(FILM_ID, arguments?.getParcelable<Film>(BUNDLE_EXTRA)?.id)
            })
        }

        _binding = FilmDetailFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun renderFilm(filmDetailDTO: FilmDetailDTO) {
        val currentFilm = arguments?.getParcelable<Film>(BUNDLE_EXTRA)
        binding.apply {
            filmDetailDTO.let { fDTO ->
                filmNameFull.text = fDTO.title
                Glide
                    .with(root)
                    .load(currentFilm?.posterPath)
                    .into(posterFull)
                countryAndYearFilmFull.text =
                    "${
                        fDTO.production_countries?.let { it1 ->
                            countriesParser(
                                it1
                            )
                        }
                    }, ${currentFilm?.year.toString()}"
                genre.text = fDTO.genres?.let { it1 -> genresParser(it1) }
                overviewFull.text = fDTO.overview
                budget.text = String.format("Бюджет: %d $", fDTO.budget)
                rating.text = String.format("Рейтинг: %.1f", fDTO.vote_average)
            }
        }
    }

    private fun genresParser(genres: List<GenreDTO>): String {
        val genresString = mutableListOf<String>()
        for (genre in genres) {
            genre.name?.let { genresString.add(it) }
        }
        return genresString
            .toString()
            .replace("[", "", true)
            .replace("]", "", true)
    }

    private fun countriesParser(countries: List<CountryDTO>): String {
        val countriesString = mutableListOf<String>()
        for (country in countries) {
            country.name?.let { countriesString.add(it) }
        }
        return countriesString
            .toString()
            .replace("[", "", true)
            .replace("]", "", true)
    }

    companion object {
        const val BUNDLE_EXTRA = "FILM"
        fun newInstance(bundle: Bundle) = FilmDetailFragment().apply { arguments = bundle }
    }
}