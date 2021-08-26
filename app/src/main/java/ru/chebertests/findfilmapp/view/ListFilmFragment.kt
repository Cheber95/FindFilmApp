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
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.FilmListFragmentBinding
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.viewmodel.FilmListAdapter
import ru.chebertests.findfilmapp.viewmodel.FilmsViewModel
import ru.chebertests.findfilmapp.viewmodel.MainViewModel

private const val BASE_LINK = "https://api.themoviedb.org/3/discover/movie?api_key=85a6977294202ae5da2d96ff6d2ed326&language=ru-RU&sort_by=vote_count.desc&include_adult=true&include_video=false&page=1&with_watch_monetization_types=flatrate"

class ListFilmFragment : Fragment() {

    private var mViewModel: MainViewModel? = null
    private var _binding: FilmListFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = FilmListAdapter()

    private val viewModel: FilmsViewModel by lazy {
        ViewModelProvider(this).get(FilmsViewModel::class.java)
    }

    interface OnFilmClickListener {
        fun onFilmClick(film: Film)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmListFragmentBinding.inflate(inflater, container, false)

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            renderList(it)
        })
        viewModel.getListFilmFromRemote(BASE_LINK)

        return binding.root
    }

    private fun renderList(state: AppState) {
        when(state) {
            is AppState.Success -> {
                adapter.setFilmData(state.listFilms)
                adapter.setFilmListener(object : OnFilmClickListener {
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
                binding.listOfFilms.adapter = adapter
                binding.listOfFilms.layoutManager =
                    LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

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

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun newInstance() = ListFilmFragment()
    }
}