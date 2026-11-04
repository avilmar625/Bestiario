package com.example.bestiario

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MonstruoUsuarioDao {
    @Insert
    suspend fun insertar(monstruo: MonstruoUsuario): Long

    @Update
    suspend fun actualizar(monstruo: MonstruoUsuario)

    @Delete
    suspend fun borrar(monstruo: MonstruoUsuario)

    @Query("SELECT * FROM monstruos_usuario ORDER BY id ASC")
    suspend fun obtenerTodos(): List<MonstruoUsuario>

    @Query("SELECT * FROM monstruos_usuario WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): MonstruoUsuario?

    @Query("SELECT COUNT(*) FROM monstruos_usuario WHERE LOWER(nombre) = LOWER(:nombre) AND id != :id")
    suspend fun contarNombreDuplicado(nombre: String, id: Int = -1): Int
}
