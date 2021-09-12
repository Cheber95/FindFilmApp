package ru.chebertests.findfilmapp.view

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.FilmListFragmentBinding
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.viewmodel.FilmListAdapter
import ru.chebertests.findfilmapp.viewmodel.FilmsViewModel

private const val IS_ADULT = "IS_ADULT"

class ListFilmFragment : Fragment() {

    private var _binding: FilmListFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = FilmListAdapter()
    private var genresList: List<GenreDTO> = listOf()
    private val adaptersByGenre: MutableList<FilmListAdapter> = mutableListOf()
    private val titlesGenre: MutableList<MaterialTextView> = mutableListOf()
    private var isAdult: Boolean = true
    private val defaultPage: Int = 1

    private val viewModel: FilmsViewModel by lazy {
        ViewModelProvider(this).get(FilmsViewModel::class.java)
    }

    interface OnFilmClickListener {
        fun onFilmClick(film: Film)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FilmListFragmentBinding.inflate(inflater, container, false)

        activity?.let {
            isAdult = it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_ADULT, false)
            if (isAdult) {
                binding.adultFAB.setImageResource(R.drawable.ic_adult_enabled)
            } else {
                binding.adultFAB.setImageResource(R.drawable.ic_adult_disabled)
            }
        }

        binding.adultFAB.setOnClickListener {
            if (isAdult) {
                binding.adultFAB.setImageResource(R.drawable.ic_adult_disabled)
            } else {
                binding.adultFAB.setImageResource(R.drawable.ic_adult_enabled)
            }
            isAdult = !isAdult

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.let {
                it.putBoolean(IS_ADULT, isAdult)
                it.apply()
            }

            adapter::removeListener
            for (adapter in adaptersByGenre) {
                adapter.removeListener()
            }
            binding.listsContainer.removeAllViews()
            viewModel.getListFilmFromRemote(null, defaultPage, isAdult)
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })
        viewModel.getListFilmFromRemote(null, defaultPage, isAdult)

        return binding.root
    }

    private fun renderData(state: AppState) {
        when (state) {
            is AppState.SuccessOnListByGenre -> {
                for (genre in genresList) {
                    if (genre.id == state.genreID) {
                        val index = genresList.indexOf(genre)
                        adaptersByGenre[index].setFilmData(state.listFilms)
                        adaptersByGenre[index].setFilmListener(filmClickListener)
                        if (genre != genresList.last()) {
                            viewModel.getListFilmFromRemote(
                                genresList[index + 1].id.toString(),
                                defaultPage,
                                isAdult
                            )
                        } else {
                            if (binding.loadingBar.visibility != View.GONE) {
                                binding.loadingBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            is AppState.SuccessOnList -> {
                adapter.setFilmData(state.listFilms)
                adapter.setFilmListener(filmClickListener)
                binding.listOfFilms.adapter = adapter
                binding.listOfFilms.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                viewModel.getGenresListFromRemote()
            }
            is AppState.SuccessOnGenres -> {
                for (genre in state.genres) {
                    genresList = state.genres
                    val index = state.genres.indexOf(genre)
                    titlesGenre.add(
                        MaterialTextView(binding.listsContainer.context).apply {
                            text = genre.name?.replaceFirstChar { it.uppercase() }
                            layoutParams = binding.listsContainer.layoutParams
                            setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                        }
                    )
                    adaptersByGenre.add(
                        FilmListAdapter()
                    )
                    with(binding.listsContainer) {
                        addView(titlesGenre[index])
                        addView(RecyclerView(context).apply {
                            adapter = adaptersByGenre[index]
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        })
                    }
                }
                viewModel.getListFilmFromRemote(
                    state.genres.first().id.toString(),
                    defaultPage,
                    isAdult
                )
            }
            is AppState.Loading -> {
                if (binding.loadingBar.visibility != View.VISIBLE) {
                    binding.loadingBar.visibility = View.VISIBLE
                }
            }
            is AppState.Error -> {
                Toast.makeText(
                    context,
                    "Ошибка загрузки данных. Попробуем ещё раз",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.getListFilmFromRemote(null, defaultPage, isAdult)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter::removeListener
        for (adapter in adaptersByGenre) {
            adapter::removeListener
        }
        binding.listsContainer.removeAllViews()
        _binding = null
    }

    private val filmClickListener = object : OnFilmClickListener {
        override fun onFilmClick(film: Film) {
            val manager = parentFragmentManager
            val bundle = Bundle()
            bundle.putParcelable(FilmDetailFragment.BUNDLE_EXTRA, film)
            manager
                .beginTransaction()
                .replace(R.id.container_general, FilmDetailFragment.newInstance(bundle))
                .addToBackStack(FilmDetailFragment.BUNDLE_EXTRA)
                .commit()
        }
    }

    companion object {
        fun newInstance() = ListFilmFragment()
    }
}