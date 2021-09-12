package ru.chebertests.findfilmapp.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.FragmentRandomFilmBinding
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.viewmodel.FilmsViewModel
import java.time.format.DateTimeFormatter

class RandomFilmFragment : Fragment() {

    private var _binding: FragmentRandomFilmBinding? = null
    private val binding get() = _binding!!
    private var randomPage = -1

    private val viewModel: FilmsViewModel by lazy {
        ViewModelProvider(this).get(FilmsViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRandomFilmBinding.inflate(
            inflater
        )

        if (binding.loadingBarDetail.visibility != View.VISIBLE) {
            binding.loadingBarDetail.visibility = View.VISIBLE
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })
        viewModel.getListFilmFromRemote(null, 1, true)

        return inflater.inflate(R.layout.fragment_random_film, container, false)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderData(state: AppState) {
        when (state) {
            is AppState.SuccessOnList -> {
                if (randomPage == -1) {
                    randomPage = (Math.random() * viewModel.getNumberOfPages()).toInt()
                    viewModel.getListFilmFromRemote(null, randomPage, true)
                } else {
                    val currentNumberFilm = (Math.random() * (state.listFilms.size - 1)).toInt()
                    val bundle = Bundle()
                    bundle.putParcelable(
                        FilmDetailFragment.BUNDLE_EXTRA,
                        state.listFilms[currentNumberFilm]
                    )
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.container_general, FilmDetailFragment.newInstance(bundle))
                        .addToBackStack(FilmDetailFragment.BUNDLE_EXTRA)
                        .commit()
                }
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
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance() =
            RandomFilmFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}