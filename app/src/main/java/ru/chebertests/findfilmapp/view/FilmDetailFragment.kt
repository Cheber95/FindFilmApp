package ru.chebertests.findfilmapp.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmDetailFragmentBinding
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.viewmodel.FilmsViewModel
import java.time.format.DateTimeFormatter

private const val PROCESS_ERROR = "Обработка ошибки"

class FilmDetailFragment : Fragment() {

    private var _binding: FilmDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilmsViewModel by lazy {
        ViewModelProvider(this).get(FilmsViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FilmDetailFragmentBinding.inflate(inflater, container, false)

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            renderFilm(it)
        })
        arguments?.getParcelable<Film>(BUNDLE_EXTRA)?.let { viewModel.getFilmDetailFromRemote(it) }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderFilm(state: AppState?) {
        when (state) {
            is AppState.SuccessOnFilm -> {
                val film = state.filmDetail
                val dateFormatter = DateTimeFormatter.ofPattern("d LLLL yyyy")
                binding.apply {
                    filmNameFull.text = film.name
                    Glide
                        .with(root)
                        .load(film.posterPath)
                        .into(posterFull)
                    countryFilmFull.text = film.countries
                    dateFilmFull.text = "Премьера: ${film.releaseDate.format(dateFormatter)}"
                    genre.text = film.genres
                    overviewFull.text = film.overview
                    budget.text = String.format("Бюджет: %d $", film.budget)
                    rating.text = String.format("Рейтинг: %.1f", film.voteAverage)
                    if (binding.loadingBarDetail.visibility != View.GONE) {
                        binding.loadingBarDetail.visibility = View.GONE
                    }
                }
                Thread {
                    viewModel.saveFilmToDB(state.filmDetail)
                }.start()
            }
            is AppState.Loading -> {
                if (binding.loadingBarDetail.visibility != View.VISIBLE) {
                    binding.loadingBarDetail.visibility = View.VISIBLE
                }
            }
            is AppState.Error -> {
                Toast.makeText(
                    context,
                    "Ошибка загрузки данных. Попробуем ещё раз",
                    Toast.LENGTH_SHORT
                ).show()
                arguments?.getParcelable<Film>(BUNDLE_EXTRA)?.let { viewModel.getFilmDetailFromRemote(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val BUNDLE_EXTRA = "FILM"
        fun newInstance(bundle: Bundle) = FilmDetailFragment().apply { arguments = bundle }
    }
}