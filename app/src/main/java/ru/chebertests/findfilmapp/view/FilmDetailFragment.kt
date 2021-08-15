package ru.chebertests.findfilmapp.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmDetailFragmentBinding
import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.model.repository.GenresRepository

class FilmDetailFragment : Fragment() {

    private var _binding: FilmDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val genresRepository = GenresRepository()
    private lateinit var genres: List<GenreDTO>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmDetailFragmentBinding.inflate(inflater, container, false)
        genresRepository.getGenres(callback = object : Callback<List<GenreDTO>> {
            override fun onSuccess(type: List<GenreDTO>) {
                genres = type

                arguments?.getParcelable<Film>(BUNDLE_EXTRA)?.let {
                    binding.apply {
                        filmNameFull.text = it.name
                        Glide
                            .with(root)
                            .load(it.posterPath.toUri())
                            .into(posterFull)
                        countryAndYearFilmFull.text = "${it.country}, ${it.year.toString()}"
                        genre.text = genresParser(it.genreIds)
                        overviewFull.text = it.overview
                    }
                }
            }
        })

        return binding.root
    }

    private fun genresParser(genreIds: List<Int>): String {
        val genresString = mutableListOf<String>()
        for (genreID in genreIds) {
            for (genre in genres)
                if (genre.id == genreID) {
                    genre.name?.let { genresString.add(it) }
                }
        }
        return genresString.toString()
    }

    companion object {
        const val BUNDLE_EXTRA = "FILM"
        fun newInstance(bundle: Bundle) = FilmDetailFragment().apply { arguments = bundle }
    }
}