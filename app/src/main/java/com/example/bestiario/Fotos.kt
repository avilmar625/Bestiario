package com.example.bestiario

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object Fotos {

    fun crearArchivo(context: Context): File {
        val carpeta = File(context.filesDir, "fotos")
        carpeta.mkdirs()
        return File(carpeta, "foto_" + System.currentTimeMillis() + ".jpg")
    }

    fun uriDe(context: Context, archivo: File): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", archivo)
    }

    // Copia una imagen elegida de la galería a la carpeta de la app y devuelve su ruta
    fun copiarDesdeUri(context: Context, uri: Uri): String? {
        return try {
            val destino = crearArchivo(context)
            context.contentResolver.openInputStream(uri)?.use { entrada ->
                destino.outputStream().use { salida -> entrada.copyTo(salida) }
            }
            destino.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    // Carga la foto reducida (1/4 de tamaño) para no gastar memoria
    fun cargar(ruta: String?): Bitmap? {
        if (ruta == null) return null
        if (!File(ruta).exists()) return null
        val opciones = BitmapFactory.Options()
        opciones.inSampleSize = 4
        return BitmapFactory.decodeFile(ruta, opciones)
    }
}