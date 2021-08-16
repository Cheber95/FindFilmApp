package ru.chebertests.findfilmapp.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.FilmListFragmentBinding
import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.GenreDTO
import ru.chebertests.findfilmapp.model.dto.GenresDTO
import ru.chebertests.findfilmapp.model.repository.FilmLocalRepository
import ru.chebertests.findfilmapp.model.repository.FilmRemoteRepository
import ru.chebertests.findfilmapp.model.repository.GenresRepository
import ru.chebertests.findfilmapp.viewmodel.FilmListAdapter
import ru.chebertests.findfilmapp.viewmodel.MainViewModel

class ListFilmFragment : Fragment() {

    private var mViewModel: MainViewModel? = null
    private var _binding: FilmListFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = FilmListAdapter()
    private val genresRepository = GenresRepository()

    interface OnFilmClickListener {
        fun onFilmClick(film: Film)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmListFragmentBinding.inflate(inflater, container, false)

        //val localRepository = FilmLocalRepository()
        val remoteRepository = FilmRemoteRepository()

        with(adapter){
            remoteRepository.getData(callback = object : Callback<List<Film>> {
                override fun onSuccess(type: List<Film>) {
                    setFilmData(type)
                }
            },null)
            setFilmListener(object : OnFilmClickListener {
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
            })
        }

        binding.listOfFilms.also {
            it.adapter = this.adapter
            it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        genresRepository.getGenres(callback = object : Callback<List<GenreDTO>> {
            override fun onSuccess(type: List<GenreDTO>) {
                for (genre in type) {
                    val subtitle = MaterialTextView(binding.listsContainer.context).apply {
                        text = genre.name?.replaceFirstChar { it.uppercase() }
                        layoutParams = binding.listsContainer.layoutParams
                        setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                    }
                    binding.listsContainer.addView(subtitle)
                    //var adapter = FilmListAdapter()
                    val remoteRepositoryByGenre = FilmRemoteRepository()
                    val genreAdapter = FilmListAdapter()
                    with(genreAdapter){
                        remoteRepositoryByGenre.getData(callback = object : Callback<List<Film>> {
                            override fun onSuccess(type: List<Film>) {
                                setFilmData(type)
                            }
                        },genre.id)
                        setFilmListener(object : OnFilmClickListener {
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
                        })
                    }

                    binding.listsContainer.addView(RecyclerView(binding.listsContainer.context).apply {
                        adapter = genreAdapter
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    })
                }
            }
        })
        //binding.listsContainer.addView(context?.let { RecyclerView(it) })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter::removeListener
    }

    companion object {
        fun newInstance() = ListFilmFragment()
    }
}