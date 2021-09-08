package ru.chebertests.findfilmapp.app

import android.app.Application
import androidx.room.Room
import ru.chebertests.findfilmapp.model.room.FilmDAO
import ru.chebertests.findfilmapp.model.room.FilmDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: App? = null
        private var db: FilmDatabase? = null
        private const val DB_NAME = "History.db"

        fun getHistoryDao(): FilmDAO {
            if (db == null) {
                synchronized(FilmDatabase::class.java) {
                    if (db == null) {
                        if (appInstance == null) throw IllegalStateException("Application is null while creating DataBase")
                        db = Room.databaseBuilder(
                            appInstance!!.applicationContext,
                            FilmDatabase::class.java,
                            DB_NAME
                        )
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return db!!.filmDAO()
        }
    }

}