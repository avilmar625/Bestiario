package com.example.bestiario

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.bestiario.data.BestiarioRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Resume la actividad del usuario a partir de los registros almacenados en Room.
 */
class EstadisticasActivity : ComponentActivity() {

    private val repositorio by lazy { BestiarioRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        findViewById<Button>(R.id.btnVolverEstadisticas).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        lifecycleScope.launch {
            val registros = repositorio.obtenerAvistamientos()
            val total = registros.size
            val especies = registros.map { it.monstruo }.distinct().size
            val lugares = registros.map { it.lugar.trim() }
                .filter { it.isNotEmpty() }
                .distinctBy { it.lowercase(Locale.ROOT) }
                .size

            val masVisto = registros.groupingBy { it.monstruo }
                .eachCount()
                .maxByOrNull { it.value }

            val ultimo = registros.maxByOrNull { it.fecha }
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.getDefault())

            findViewById<TextView>(R.id.tvTotalAvistamientos).text = total.toString()
            findViewById<TextView>(R.id.tvEspeciesDistintas).text = especies.toString()
            findViewById<TextView>(R.id.tvLugaresVisitados).text = lugares.toString()
            findViewById<TextView>(R.id.tvMonstruoMasVisto).text =
                masVisto?.let { "${it.key} (${it.value})" } ?: "Todavía no hay datos"
            findViewById<TextView>(R.id.tvUltimoAvistamiento).text =
                ultimo?.let { "${it.monstruo} · ${formatoFecha.format(Date(it.fecha))}" }
                    ?: "Registra tu primer avistamiento para ver estadísticas"
        }
    }
}
