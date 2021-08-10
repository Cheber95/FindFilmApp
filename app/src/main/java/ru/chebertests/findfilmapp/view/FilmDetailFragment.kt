package ru.chebertests.findfilmapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmDetailFragmentBinding
import ru.chebertests.findfilmapp.model.Film

class FilmDetailFragment : Fragment() {

    private var _binding: FilmDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmDetailFragmentBinding.inflate(inflater, container, false)

        val film = arguments?.getParcelable<Film>(BUNDLE_EXTRA)

        binding.filmNameFull.text = film!!.name
        Glide
            .with(binding.root)
            .load(film.posterPath.toUri())
            .into(binding.posterFull)
        binding.countryAndYearFilmFull.text = "${film.country}, ${film.year.toString()}"
        binding.genre.text = film.genreIds.toString()
        binding.overviewFull.text = getString(film.overview.toInt())

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {

        const val BUNDLE_EXTRA = "FILM"

        fun newInstance(bundle: Bundle): FilmDetailFragment {
            val fragment = FilmDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}