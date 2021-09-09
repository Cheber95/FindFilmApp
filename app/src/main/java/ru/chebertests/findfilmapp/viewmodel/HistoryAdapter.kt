package ru.chebertests.findfilmapp.viewmodel

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmCardItemBinding
import ru.chebertests.findfilmapp.model.FilmDetail

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var data: List<FilmDetail> = mutableListOf()

    fun setData(newData: List<FilmDetail>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: FilmCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(filmDetail: FilmDetail) {
            with(binding) {
                nameOfFilm.text = filmDetail.name
                filmDate.text = filmDetail.releaseDate.year.toString()
                filmRating.text = filmDetail.voteAverage.toString()

                Glide
                    .with(root.context)
                    .load(filmDetail.posterPath.toUri())
                    .into(posterFilm)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = FilmCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}