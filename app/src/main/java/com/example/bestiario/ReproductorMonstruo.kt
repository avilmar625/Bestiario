package com.example.bestiario

import android.content.Context
import android.media.MediaPlayer

// Encapsula la creación, reproducción y liberación del sonido de un monstruo.
class ReproductorMonstruo(context: Context) {

    private val contexto = context.applicationContext
    private var reproductor: MediaPlayer? = null

    fun reproducir(sonidoRes: Int?) {
        liberar()
        if (sonidoRes == null) return

        reproductor = MediaPlayer.create(contexto, sonidoRes)?.also { nuevo ->
            nuevo.setOnCompletionListener { terminado ->
                terminado.release()
                if (reproductor === terminado) reproductor = null
            }
            nuevo.start()
        }
    }

    fun liberar() {
        reproductor?.release()
        reproductor = null
    }
}
