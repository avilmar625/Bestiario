package com.example.bestiario

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Avistamiento::class, MonstruoUsuario::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun avistamientoDao(): AvistamientoDao
    abstract fun monstruoUsuarioDao(): MonstruoUsuarioDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bestiario_db"
                ).build().also { nueva ->
                    instancia = nueva
                }
            }
        }
    }
}
