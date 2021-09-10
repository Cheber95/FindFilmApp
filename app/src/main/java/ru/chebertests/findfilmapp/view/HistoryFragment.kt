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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.chebertests.findfilmapp.databinding.FilmDetailFragmentBinding
import ru.chebertests.findfilmapp.databinding.HistoryFragmentBinding
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.viewmodel.FilmsViewModel
import ru.chebertests.findfilmapp.viewmodel.HistoryAdapter
import ru.chebertests.findfilmapp.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private var _binding: HistoryFragmentBinding? = null
    private val binding get() = _binding!!
    private val adapter: HistoryAdapter = HistoryAdapter()
    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HistoryFragmentBinding.inflate(inflater, container, false)

        binding.historyOfFilms.adapter = adapter
        binding.historyOfFilms.layoutManager =
            LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)

        binding.clearHistory.setOnClickListener {
            viewModel.deleteAllHistory()
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })
        viewModel.getAllHistory()

        return binding.root
    }

    private fun renderData(state: AppState?) {
        when (state) {
            is AppState.SuccessOnHistory -> {
                binding.loadingBar.visibility = View.GONE
                adapter.setData(state.history)
            }
            is AppState.Loading -> {
                binding.loadingBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val BUNDLE_EXTRA = "HISTORY"
        fun newInstance() = HistoryFragment()
    }

}