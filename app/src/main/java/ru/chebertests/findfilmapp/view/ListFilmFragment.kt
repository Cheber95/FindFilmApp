package ru.chebertests.findfilmapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.FilmListFragmentBinding
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.repository.FilmLocalRepository
import ru.chebertests.findfilmapp.viewmodel.FilmListAdapter
import ru.chebertests.findfilmapp.viewmodel.MainViewModel

class ListFilmFragment : Fragment() {

    private var mViewModel: MainViewModel? = null
    private var _binding: FilmListFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = FilmListAdapter()

    interface OnFilmClickListener {
        fun onFilmClick(film: Film)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmListFragmentBinding.inflate(inflater, container, false)

        val localRepository = FilmLocalRepository()

        with(adapter){
            setFilmData(localRepository.getData())
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