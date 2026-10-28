package com.example.bestiario

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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

        // Registrar avistamiento (Room)
        val etLugar = findViewById<EditText>(R.id.etLugar)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val dao = AppDatabase.obtener(this).avistamientoDao()

        btnRegistrar.setOnClickListener {
            val lugar = etLugar.text.toString().trim()
            if (lugar.isEmpty()) {
                Toast.makeText(this, "Escribe dónde lo has visto", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    dao.insertar(
                        Avistamiento(
                            monstruo = monstruo.nombre,
                            lugar = lugar,
                            fecha = System.currentTimeMillis()
                        )
                    )
                    Toast.makeText(this@DetalleActivity, "Avistamiento guardado", Toast.LENGTH_SHORT).show()
                    etLugar.text.clear()
                }
            }
        }

        // Favoritos (SharedPreferences)
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