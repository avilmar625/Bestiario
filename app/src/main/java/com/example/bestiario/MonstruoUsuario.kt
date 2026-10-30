package com.example.bestiario

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monstruos_usuario")
data class MonstruoUsuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val fotoPath: String? = null
)