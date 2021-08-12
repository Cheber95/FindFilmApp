package ru.chebertests.findfilmapp.view

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

        val film = arguments?.getParcelable<Film>(BUNDLE_EXTRA)?.let {
            binding.apply {
                filmNameFull.text = it.name
                Glide
                    .with(root)
                    .load(it.posterPath.toUri())
                    .into(posterFull)
                countryAndYearFilmFull.text = "${it.country}, ${it.year.toString()}"
                genre.text = it.genreIds.toString()
                overviewFull.text = getString(it.overview.toInt())
            }
        }

        return binding.root
    }

    companion object {
        const val BUNDLE_EXTRA = "FILM"
        fun newInstance(bundle: Bundle) = FilmDetailFragment().apply { arguments = bundle }
    }
}