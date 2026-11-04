package com.example.bestiario.data

import android.content.Context
import com.example.bestiario.AppDatabase
import com.example.bestiario.Avistamiento
import com.example.bestiario.Catalogo
import com.example.bestiario.Monstruo
import com.example.bestiario.MonstruoUsuario

class BestiarioRepository(context: Context) {
    private val appContext = context.applicationContext
    private val database = AppDatabase.obtener(appContext)

    suspend fun obtenerMonstruos(): List<Monstruo> = Catalogo.todos(appContext)
    suspend fun obtenerAvistamientos(): List<Avistamiento> = database.avistamientoDao().obtenerTodos()
    suspend fun borrarAvistamiento(avistamiento: Avistamiento) = database.avistamientoDao().borrar(avistamiento)
    suspend fun obtenerCriatura(id: Int): MonstruoUsuario? = database.monstruoUsuarioDao().obtenerPorId(id)

    suspend fun guardarCriatura(criatura: MonstruoUsuario, esEdicion: Boolean) {
        val dao = database.monstruoUsuarioDao()
        if (dao.contarNombreDuplicado(criatura.nombre, if (esEdicion) criatura.id else -1) > 0) {
            throw IllegalArgumentException("Ya existe una criatura con ese nombre")
        }
        if (esEdicion) dao.actualizar(criatura) else dao.insertar(criatura)
    }
}
