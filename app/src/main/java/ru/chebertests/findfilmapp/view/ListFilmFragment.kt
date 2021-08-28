package ru.chebertests.findfilmapp.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ru.chebertests.findfilmapp.viewmodel.FilmListAdapter
import ru.chebertests.findfilmapp.viewmodel.FilmsViewModel

class ListFilmFragment : Fragment() {

    private var _binding: FilmListFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = FilmListAdapter()
    private val adaptersByGenre: MutableList<FilmListAdapter> = mutableListOf()
    private val titlesGenre: MutableList<MaterialTextView> = mutableListOf()

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

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })
        viewModel.getGenresListFromRemote()
        viewModel.getListFilmFromRemote(null)

        return binding.root
    }

    private fun renderData(state: AppState) {
        when (state) {
            is AppState.Success -> {
                adapter.setFilmData(state.listFilms)
                adapter.setFilmListener(filmClickListener)
                binding.listOfFilms.adapter = adapter
                binding.listOfFilms.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            is AppState.SuccessOnGenres -> {
                for (genre in state.genres) {
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
                    binding.listsContainer.addView(titlesGenre[index])
                }
            }
            is AppState.Loading -> {
                //TODO("Повесить прогрессбар")
            }
            is AppState.Error -> {
                TODO("Обработать ошибку")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter::removeListener
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