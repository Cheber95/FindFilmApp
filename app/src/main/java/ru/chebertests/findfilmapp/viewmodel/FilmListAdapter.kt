package ru.chebertests.findfilmapp.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.FilmCardItemBinding
import ru.chebertests.findfilmapp.model.Film

class FilmListAdapter : RecyclerView.Adapter<FilmListAdapter.FilmViewHolder>() {

    private var filmData: List<Film> = listOf()

    fun setFilmData(newFilmData: List<Film>) {
        this.filmData = newFilmData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = FilmCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(filmData[position])
    }

    override fun getItemCount(): Int {
        return filmData.size
    }

    class FilmViewHolder(val binding: FilmCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(film: Film) {
            binding.nameOfFilm.text = film.name;
            binding.posterFilmUrl.setImageResource(R.drawable.ic_launcher_background) //TODO
        }
    }

}