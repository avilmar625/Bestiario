package com.example.bestiario

import android.content.Context

object Catalogo {

    suspend fun todos(context: Context): List<Monstruo> {
        val base = MonstruosData.lista
        val propios = AppDatabase.obtener(context).monstruoUsuarioDao().obtenerTodos()
            .map {
                Monstruo(
                    nombre = it.nombre,
                    descripcion = it.descripcion,
                    imagenRes = android.R.drawable.ic_menu_help,
                    sonidoRes = base[0].sonidoRes,
                    fotoPath = it.fotoPath
                )
            }
        return base + propios
    }
}