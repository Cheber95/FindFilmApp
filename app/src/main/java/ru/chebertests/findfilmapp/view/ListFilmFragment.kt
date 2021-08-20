package ru.chebertests.findfilmapp.view

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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

        val remoteRepository = FilmRemoteRepository()
        val handler = Handler(Looper.getMainLooper())

        with(adapter){
            Thread {
                remoteRepository.getData(callback = object : Callback<List<Film>> {
                    override fun onSuccess(result: List<Film>) {
                        setFilmData(result)
                        handler.post(Runnable {
                            binding.listOfFilms.also {
                                it.adapter = this@with
                                it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            }
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
                        })
                    }
                },null)
                genresRepository.getGenres(callback = object : Callback<List<GenreDTO>> {
                    override fun onSuccess(resultGenres: List<GenreDTO>) {
                        for (genre in resultGenres) {
                            val remoteRepositoryByGenre = FilmRemoteRepository()
                            val byGenreAdapter = FilmListAdapter()
                            with(byGenreAdapter){
                                remoteRepositoryByGenre.getData(callback = object : Callback<List<Film>> {
                                    override fun onSuccess(resultListFilm: List<Film>) {
                                        handler.post(Runnable {
                                            setFilmData(resultListFilm)
                                            val subtitle = MaterialTextView(binding.listsContainer.context).apply {
                                                text = genre.name?.replaceFirstChar { it.uppercase() }
                                                layoutParams = binding.listsContainer.layoutParams
                                                setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                                            }
                                            binding.listsContainer.addView(subtitle)
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
                                            binding.listsContainer.addView(RecyclerView(binding.listsContainer.context).apply {
                                                adapter = byGenreAdapter
                                                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                            })
                                        })
                                    }
                                },genre.id)
                            }
                        }
                    }
                })
            }.start()
        }


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