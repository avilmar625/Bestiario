package com.example.bestiario

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AvistamientoDao {

    @Insert
    suspend fun insertar(avistamiento: Avistamiento)

    @Query("SELECT * FROM avistamientos ORDER BY id DESC")
    suspend fun obtenerTodos(): List<Avistamiento>

    @Delete
    suspend fun borrar(avistamiento: Avistamiento)
}