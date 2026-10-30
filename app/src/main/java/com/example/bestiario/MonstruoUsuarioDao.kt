package com.example.bestiario

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MonstruoUsuarioDao {

    @Insert
    suspend fun insertar(monstruo: MonstruoUsuario)

    @Query("SELECT * FROM monstruos_usuario ORDER BY id ASC")
    suspend fun obtenerTodos(): List<MonstruoUsuario>

    @Delete
    suspend fun borrar(monstruo: MonstruoUsuario)
}