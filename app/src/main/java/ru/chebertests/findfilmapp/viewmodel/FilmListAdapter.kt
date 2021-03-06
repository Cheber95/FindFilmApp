package ru.chebertests.findfilmapp.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.chebertests.findfilmapp.databinding.FilmCardItemBinding
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.view.ListFilmFragment

class FilmListAdapter :
    RecyclerView.Adapter<FilmListAdapter.FilmViewHolder>() {

    private var onFilmClickListener: ListFilmFragment.OnFilmClickListener? = null
    private var filmData: List<Film> = listOf()

    fun setFilmListener(onFilmClickListener: ListFilmFragment.OnFilmClickListener?) {
        this.onFilmClickListener = onFilmClickListener
    }

    fun removeListener() {
        onFilmClickListener = null
    }

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

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) =
        holder.bind(filmData[position])

    override fun getItemCount() = filmData.size

    inner class FilmViewHolder(val binding: FilmCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(film: Film) {
            binding.apply {
                with(film) {
                    nameOfFilm.text = name
                    Glide
                        .with(root.context)
                        .load(posterPath.toUri())
                        .into(posterFilm)
                    filmRating.text = voteAverage.toString()
                    filmDate.text = year.toString()
                }
                root.setOnClickListener {
                    onFilmClickListener?.onFilmClick(film)
                }
            }
        }
    }
}
