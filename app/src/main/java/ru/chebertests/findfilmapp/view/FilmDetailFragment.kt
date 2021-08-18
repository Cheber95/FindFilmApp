package ru.chebertests.findfilmapp.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmDetailFragmentBinding
import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.CountryDTO
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.model.repository.FilmRemoteRepository

class FilmDetailFragment : Fragment() {

    private var _binding: FilmDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val filmRepository = FilmRemoteRepository()
    private lateinit var genres: List<GenreDTO>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmDetailFragmentBinding.inflate(inflater, container, false)
        val currentFilm = arguments?.getParcelable<Film>(BUNDLE_EXTRA)
        currentFilm?.let {
            filmRepository.getFilm(callback = object : Callback<FilmDetailDTO> {
                override fun onSuccess(result: FilmDetailDTO) {
                    binding.apply {
                        result.let { res ->
                            filmNameFull.text = res.title
                            Glide
                                .with(root)
                                .load(currentFilm.posterPath)
                                .into(posterFull)
                            countryAndYearFilmFull.text =
                                "${res.production_countries?.let { it1 -> countriesParser(it1) }}, ${it.year.toString()}"
                            genre.text = res.genres?.let { it1 -> genresParser(it1) }
                            overviewFull.text = res.overview
                            budget.text = String.format("Бюджет: %d $", res.budget)
                            rating.text = String.format("Рейтинг: %.1f", res.vote_average)
                        }
                    }
                }
            }, it.id)
        }

        return binding.root
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