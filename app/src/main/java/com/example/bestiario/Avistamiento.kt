package com.example.bestiario

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avistamientos")
data class Avistamiento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monstruo: String,
    val lugar: String,
    val fecha: Long,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoPath: String? = null
)