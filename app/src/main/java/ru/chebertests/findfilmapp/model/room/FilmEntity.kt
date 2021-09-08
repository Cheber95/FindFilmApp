package ru.chebertests.findfilmapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class FilmEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val posterPath: String,
    val voteAverage: Double,
    val releaseDate: LocalDate,
    val budget: Int,
    val genres: String,
    val overview: String,
    val countries: String
)