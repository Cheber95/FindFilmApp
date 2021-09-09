package ru.chebertests.findfilmapp.model.room

import androidx.room.*

@Dao
interface FilmDAO {

    @Query("SELECT * FROM FilmEntity")
    fun all(): List<FilmEntity>

    @Query("SELECT * FROM FilmEntity WHERE name LIKE :name")
    fun getDataByWord(name: String): List<FilmEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: FilmEntity)

    @Update
    fun update(entity: FilmEntity)

    @Delete
    fun delete(entity: FilmEntity)

}