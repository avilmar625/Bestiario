package com.example.bestiario.data

import android.content.Context
import com.example.bestiario.AppDatabase
import com.example.bestiario.Avistamiento
import com.example.bestiario.Catalogo
import com.example.bestiario.Monstruo

/**
 * Punto de acceso a los datos que necesitan las pantallas.
 *
 * La interfaz no necesita conocer cómo se combinan los monstruos del catálogo
 * ni cómo se consulta Room: solicita los datos a este repositorio.
 */
class BestiarioRepository(context: Context) {

    private val appContext = context.applicationContext
    private val database = AppDatabase.obtener(appContext)

    suspend fun obtenerMonstruos(): List<Monstruo> =
        Catalogo.todos(appContext)

    suspend fun obtenerAvistamientos(): List<Avistamiento> =
        database.avistamientoDao().obtenerTodos()

    suspend fun borrarAvistamiento(avistamiento: Avistamiento) {
        database.avistamientoDao().borrar(avistamiento)
    }
}
