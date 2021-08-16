package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.Film

class FilmLocalRepository : IFilmRepository {

    override fun getData(callback: Callback<List<Film>>, genreID: Int?) {
        callback.onSuccess(
            listOf<Film>(
                Film(
                    0,
                    "Бойцовский клуб",
                    "https://cdn1.ozone.ru/s3/multimedia-c/6016453560.jpg",
                    R.string.fight_club_overview.toString(),
                    1999,
                    "США, Германия",
                    listOf(1, 2, 3)
                ),
                Film(
                    1,
                    "Д`Артаньян и три мушкетера",
                    "https://www.timeout.ru/wp-content/uploads/serials/44076.jpg",
                    R.string.three_mush_overview.toString(),
                    1979,
                    "СССР",
                    listOf(4, 5, 6)
                ),
                Film(
                    2,
                    "Судный день",
                    "https://img11.postila.ru/data/ae/53/52/dc/ae5352dc431a3442dd286a182cffa7acaf92df248eefc14b0c2ad770114ee8c0.jpg",
                    R.string.doomsday_overview.toString(),
                    2008,
                    "Великобритания",
                    listOf(7, 8, 2)
                ),
                Film(
                    0,
                    "Бойцовский клуб",
                    "https://cdn1.ozone.ru/s3/multimedia-c/6016453560.jpg",
                    R.string.fight_club_overview.toString(),
                    1999,
                    "США, Германия",
                    listOf(1, 2, 3)
                ),
                Film(
                    1,
                    "Д`Артаньян и три мушкетера",
                    "https://www.timeout.ru/wp-content/uploads/serials/44076.jpg",
                    R.string.three_mush_overview.toString(),
                    1979,
                    "СССР",
                    listOf(4, 5, 6)
                ),
                Film(
                    2,
                    "Судный день",
                    "https://img11.postila.ru/data/ae/53/52/dc/ae5352dc431a3442dd286a182cffa7acaf92df248eefc14b0c2ad770114ee8c0.jpg",
                    R.string.doomsday_overview.toString(),
                    2008,
                    "Великобритания",
                    listOf(7, 8, 2)
                )
            )
        )
    }

}