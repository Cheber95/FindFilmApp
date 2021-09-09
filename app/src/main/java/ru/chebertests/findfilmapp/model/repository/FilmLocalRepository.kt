package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.Film
import ru.chebertests.findfilmapp.model.dto.FilmDetailDTO

class FilmLocalRepository : IFilmRepository {

    override fun getData(callback: Callback<List<Film>>, genreID: Int?) {
        callback.onSuccess(
            listOf<Film>(
                Film(
                    0,
                    "Бойцовский клуб",
                    "https://cdn1.ozone.ru/s3/multimedia-c/6016453560.jpg",
                    8.1,
                    1999
                ),
                Film(
                    1,
                    "Д`Артаньян и три мушкетера",
                    "https://www.timeout.ru/wp-content/uploads/serials/44076.jpg",
                    7.6,
                    1979
                ),
                Film(
                    2,
                    "Судный день",
                    "https://img11.postila.ru/data/ae/53/52/dc/ae5352dc431a3442dd286a182cffa7acaf92df248eefc14b0c2ad770114ee8c0.jpg",
                    6.5,
                    2008
                ),
                Film(
                    0,
                    "Бойцовский клуб",
                    "https://cdn1.ozone.ru/s3/multimedia-c/6016453560.jpg",
                    4.5,
                    1999
                ),
                Film(
                    1,
                    "Д`Артаньян и три мушкетера",
                    "https://www.timeout.ru/wp-content/uploads/serials/44076.jpg",
                    9.7,
                    1979
                ),
                Film(
                    2,
                    "Судный день",
                    "https://img11.postila.ru/data/ae/53/52/dc/ae5352dc431a3442dd286a182cffa7acaf92df248eefc14b0c2ad770114ee8c0.jpg",
                    3.7,
                    2008
                )
            )
        )
    }

    override fun getFilm(callback: Callback<FilmDetailDTO>, filmID: Int) {
        TODO("Not yet implemented")
    }

}