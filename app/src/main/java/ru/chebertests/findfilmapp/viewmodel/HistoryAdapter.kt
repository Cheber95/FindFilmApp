package ru.chebertests.findfilmapp.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmCardItemBinding
import ru.chebertests.findfilmapp.databinding.FilmHistoryCardItemBinding
import ru.chebertests.findfilmapp.model.FilmDetail
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var data: List<FilmDetail> = mutableListOf()

    fun setData(newData: List<FilmDetail>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: FilmHistoryCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(filmDetail: FilmDetail) {
            with(binding) {
                nameOfFilm.text = filmDetail.name
                Glide
                    .with(root)
                    .load(filmDetail.posterPath)
                    .into(posterFilm)
                filmHistoryCountries.text = filmDetail.countries
                historyFilmPremiere.text = "Премьера: ${
                    filmDetail.releaseDate.format(
                        DateTimeFormatter.ofPattern("d LLLL yyyy")
                    )
                }"
                historyFilmGenres.text = filmDetail.genres
                filmHistoryOverview.text = "Описание: ${filmDetail.overview}"
                historyFilmBudget.text = String.format("Бюджет: %d $", filmDetail.budget)
                filmRating.text = filmDetail.voteAverage.toString()
                timeOnClicked.text = Date(filmDetail.getTimeLong()).toString()
                filmHistoryNote.text = "Заметка: ${filmDetail.getNote()}"
            }
            binding.timeOnClicked
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = FilmHistoryCardItemBinding.inflate(
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