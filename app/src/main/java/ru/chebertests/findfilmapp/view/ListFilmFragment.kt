package ru.chebertests.findfilmapp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import ru.chebertests.findfilmapp.model.services.LoadFilmService
import ru.chebertests.findfilmapp.viewmodel.FilmListAdapter
import ru.chebertests.findfilmapp.viewmodel.MainViewModel

private const val LOAD_RESULT_EXTRA = "LOAD RESULT"
private const val LOAD_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
private const val LOAD_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
private const val LOAD_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
private const val LOAD_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val GENRES_LOAD_SUCCESS_EXTRA = "GENRES LOAD SUCCESS EXTRA"
private const val PROCESS_ERROR = "Обработка ошибки"

class ListFilmFragment : Fragment() {

    private var mViewModel: MainViewModel? = null
    private var _binding: FilmListFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = FilmListAdapter()
    private val genresRepository = GenresRepository()

    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra(LOAD_RESULT_EXTRA)) {
                LOAD_INTENT_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                LOAD_DATA_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                LOAD_REQUEST_ERROR_EXTRA -> TODO(PROCESS_ERROR)
                LOAD_URL_MALFORMED_EXTRA -> TODO(PROCESS_ERROR)
                GENRES_LOAD_SUCCESS_EXTRA -> {

                    @Suppress("UNCHECKED_CAST")
                    genresRepository.setGenres(
                        intent
                            .getParcelableArrayExtra(GENRES_LOAD_SUCCESS_EXTRA)
                            ?.toList() as List<GenreDTO>)
                    //Toast.makeText(context,"ХУЙ",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    interface OnFilmClickListener {
        fun onFilmClick(film: Film)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(LOAD_INTENT_FILTER))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmListFragmentBinding.inflate(inflater, container, false)

        val remoteRepository = FilmRemoteRepository()
        val handler = Handler(Looper.getMainLooper())

        context?.let {
            it.startService(Intent(it, LoadFilmService::class.java).apply {
                putExtra(COMMAND, TO_LOAD_GENRES)
            })
        }

        with(adapter) {
            Thread {
                remoteRepository.getData(callback = object : Callback<List<Film>> {
                    override fun onSuccess(result: List<Film>) {
                        setFilmData(result)
                        handler.post(Runnable {
                            binding.listOfFilms.also {
                                it.adapter = this@with
                                it.layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                            }
                            setFilmListener(object : OnFilmClickListener {
                                override fun onFilmClick(film: Film) {
                                    val manager = parentFragmentManager
                                    val bundle = Bundle()
                                    bundle.putParcelable(FilmDetailFragment.BUNDLE_EXTRA, film)
                                    manager
                                        .beginTransaction()
                                        .replace(
                                            R.id.container_general,
                                            FilmDetailFragment.newInstance(bundle)
                                        )
                                        .addToBackStack(FilmDetailFragment.BUNDLE_EXTRA)
                                        .commit()
                                }
                            })
                        })
                    }
                }, null)
                genresRepository.getGenres(callback = object : Callback<List<GenreDTO>> {
                    override fun onSuccess(resultGenres: List<GenreDTO>) {
                        for (genre in resultGenres) {
                            val remoteRepositoryByGenre = FilmRemoteRepository()
                            val byGenreAdapter = FilmListAdapter()
                            with(byGenreAdapter) {
                                remoteRepositoryByGenre.getData(callback = object :
                                    Callback<List<Film>> {
                                    override fun onSuccess(resultListFilm: List<Film>) {
                                        handler.post(Runnable {
                                            setFilmData(resultListFilm)
                                            val subtitle =
                                                MaterialTextView(binding.listsContainer.context).apply {
                                                    text =
                                                        genre.name?.replaceFirstChar { it.uppercase() }
                                                    layoutParams =
                                                        binding.listsContainer.layoutParams
                                                    setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                                                }
                                            binding.listsContainer.addView(subtitle)
                                            setFilmListener(object : OnFilmClickListener {
                                                override fun onFilmClick(film: Film) {
                                                    val manager = parentFragmentManager
                                                    val bundle = Bundle()
                                                    bundle.putParcelable(
                                                        FilmDetailFragment.BUNDLE_EXTRA,
                                                        film
                                                    )
                                                    manager
                                                        .beginTransaction()
                                                        .replace(
                                                            R.id.container_general,
                                                            FilmDetailFragment.newInstance(bundle)
                                                        )
                                                        .addToBackStack(FilmDetailFragment.BUNDLE_EXTRA)
                                                        .commit()
                                                }
                                            })
                                            binding.listsContainer.addView(RecyclerView(binding.listsContainer.context).apply {
                                                adapter = byGenreAdapter
                                                layoutManager = LinearLayoutManager(
                                                    context,
                                                    LinearLayoutManager.HORIZONTAL,
                                                    false
                                                )
                                            })
                                        })
                                    }
                                }, genre.id)
                            }
                        }
                    }
                })
            }.start()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter::removeListener
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }

    companion object {
        fun newInstance() = ListFilmFragment()
    }
}