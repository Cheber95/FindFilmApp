package ru.chebertests.findfilmapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.FilmListFragmentBinding
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.repository.FilmLocalRepository
import ru.chebertests.findfilmapp.viewmodel.FilmListAdapter
import ru.chebertests.findfilmapp.viewmodel.MainViewModel
import ru.chebertests.findfilmapp.view.ListFilmFragment.OnFilmClickListener as OnFilmClickListener

class ListFilmFragment : Fragment() {

    private var mViewModel: MainViewModel? = null
    private var _binding: FilmListFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter = FilmListAdapter()

    interface OnFilmClickListener {
        fun onFilmClick(film: Film)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmListFragmentBinding.inflate(inflater, container, false)

        val localRepository = FilmLocalRepository()

        val recyclerListFilms = binding.listOfFilms
        adapter.setFilmData(localRepository.getData())
        recyclerListFilms.adapter = adapter
        recyclerListFilms.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapter.setFilmListener(object : OnFilmClickListener {
            override fun onFilmClick(film: Film) {
                val manager = parentFragmentManager
                val bundle = Bundle()
                bundle.putParcelable(FilmDetailFragment.BUNDLE_EXTRA,film)
                manager
                    .beginTransaction()
                    .replace(R.id.container_general, FilmDetailFragment.newInstance(bundle))
                    .addToBackStack(FilmDetailFragment.BUNDLE_EXTRA)
                    .commit()
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter.removeListener()
    }

    companion object {
        fun newInstance(): ListFilmFragment {
            return ListFilmFragment()
        }
    }
}