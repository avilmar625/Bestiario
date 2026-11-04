package com.example.bestiario

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object Fotos {

    // Todas las imágenes propias se guardan en el almacenamiento privado de la aplicación.
    fun crearArchivo(context: Context): File {
        val carpeta = File(context.filesDir, "fotos")
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw IllegalStateException("No se ha podido crear la carpeta de fotografías")
        }
        return File.createTempFile("foto_", ".jpg", carpeta)
    }

    fun uriDe(context: Context, archivo: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            archivo
        )
    }

    // Copia la imagen elegida de la galería al almacenamiento privado de la app.
    // Devuelve null si no se puede abrir o copiar el archivo.
    fun copiarDesdeUri(context: Context, uri: Uri): String? {
        var destino: File? = null
        return try {
            val archivo = crearArchivo(context)
            destino = archivo
            val entrada = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("No se ha podido abrir la imagen")

            entrada.use { origen ->
                archivo.outputStream().use { salida ->
                    origen.copyTo(salida)
                }
            }

            if (archivo.length() == 0L) {
                throw IllegalStateException("La imagen está vacía")
            }
            archivo.absolutePath
        } catch (e: Exception) {
            destino?.delete() // Evita dejar archivos incompletos.
            null
        }
    }

    // Carga una versión reducida para consumir menos memoria.
    fun cargar(ruta: String?): Bitmap? {
        if (ruta.isNullOrBlank()) return null
        val archivo = File(ruta)
        if (!archivo.isFile) return null

        val opciones = BitmapFactory.Options().apply {
            inSampleSize = 4
        }
        return BitmapFactory.decodeFile(ruta, opciones)
    }
}
