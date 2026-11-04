package com.example.bestiario

import android.content.Context

object Catalogo {

    private val originales = listOf(
        Monstruo(
            "El Coco",
            "Se esconde en los armarios de los niños que no quieren dormir.",
            R.drawable.ic_coco,
            R.raw.sonido1
        ),
        Monstruo(
            "El Sacamantecas",
            "Vaga de noche buscando a quien no haya terminado los deberes.",
            R.drawable.ic_sacamantecas,
            R.raw.sonido2
        ),
        Monstruo(
            "La Santa Compaña",
            "Procesión de almas que solo ven quienes se han dormido en clase.",
            R.drawable.ic_compana,
            R.raw.sonido3
        ),
        Monstruo(
            "El Hombre del Saco",
            "Recoge los móviles que suenan en mitad del examen.",
            R.drawable.ic_saco,
            R.raw.sonido4
        )
    )

    suspend fun todos(context: Context): List<Monstruo> {
        val propios = AppDatabase.obtener(context)
            .monstruoUsuarioDao()
            .obtenerTodos()
            .map { monstruo ->
                Monstruo(
                    nombre = monstruo.nombre,
                    descripcion = monstruo.descripcion,
                    imagenRes = android.R.drawable.ic_menu_help,
                    sonidoRes = originales.first().sonidoRes,
                    fotoPath = monstruo.fotoPath
                )
            }

        return originales + propios
    }
}
