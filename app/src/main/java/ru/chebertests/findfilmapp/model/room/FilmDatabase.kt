package ru.chebertests.findfilmapp.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FilmEntity::class), version = 2, exportSchema = false)
abstract class FilmDatabase : RoomDatabase() {

    abstract fun filmDAO() : FilmDAO

}