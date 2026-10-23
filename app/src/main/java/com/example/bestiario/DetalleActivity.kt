package com.example.bestiario

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity

class DetalleActivity : ComponentActivity() {

    private var reproductor: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        val index = intent.getIntExtra("MONSTRUO_INDEX", 0)
        val monstruo = MonstruosData.lista[index]

        findViewById<ImageView>(R.id.imagenDetalle).setImageResource(monstruo.imagenRes)
        findViewById<TextView>(R.id.nombreDetalle).text = monstruo.nombre
        findViewById<TextView>(R.id.descripcionDetalle).text = monstruo.descripcion

        reproductor = MediaPlayer.create(this, monstruo.sonidoRes)
        reproductor?.start()

        val prefs = getSharedPreferences("MisDatos", MODE_PRIVATE)
        val botonFavorito = findViewById<Button>(R.id.botonFavorito)

        fun actualizarTextoBoton() {
            val favoritos = prefs.getStringSet("FAVORITOS", setOf()) ?: setOf()
            botonFavorito.text = if (favoritos.contains(monstruo.nombre))
                "Quitar de favoritos" else "Añadir a favoritos"
        }
        actualizarTextoBoton()

        botonFavorito.setOnClickListener {
            val favoritos = (prefs.getStringSet("FAVORITOS", setOf()) ?: setOf()).toMutableSet()
            if (favoritos.contains(monstruo.nombre)) {
                favoritos.remove(monstruo.nombre)
            } else {
                favoritos.add(monstruo.nombre)
            }
            prefs.edit().putStringSet("FAVORITOS", favoritos).apply()
            actualizarTextoBoton()
        }

        findViewById<Button>(R.id.botonVolverDetalle).setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        reproductor?.release()
        reproductor = null
    }
}