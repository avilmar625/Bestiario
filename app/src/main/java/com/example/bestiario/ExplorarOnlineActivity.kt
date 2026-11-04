package com.example.bestiario

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bestiario.data.WikipediaRepository
import kotlinx.coroutines.launch

class ExplorarOnlineActivity : ComponentActivity() {
    private val repositorio = WikipediaRepository()
    private lateinit var consulta: EditText
    private lateinit var progreso: ProgressBar
    private lateinit var estado: TextView
    private lateinit var lista: RecyclerView
    private lateinit var botonBuscar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorar_online)
        consulta = findViewById(R.id.etConsultaOnline)
        progreso = findViewById(R.id.progressOnline)
        estado = findViewById(R.id.tvEstadoOnline)
        lista = findViewById(R.id.recyclerOnline)
        botonBuscar = findViewById(R.id.btnBuscarOnline)
        lista.layoutManager = LinearLayoutManager(this)
        botonBuscar.setOnClickListener { buscar() }
        consulta.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { buscar(); true } else false
        }
        findViewById<Button>(R.id.btnCerrarOnline).setOnClickListener { finish() }
        consulta.setText(intent.getStringExtra(EXTRA_CONSULTA) ?: "criaturas mitológicas")
        buscar()
    }

    private fun buscar() {
        val texto = consulta.text.toString().trim()
        if (texto.isEmpty()) { consulta.error = "Escribe una búsqueda"; return }
        botonBuscar.isEnabled = false
        progreso.visibility = View.VISIBLE
        estado.text = "Buscando en Wikipedia…"
        lifecycleScope.launch {
            try {
                val resultados = repositorio.buscar(texto)
                lista.adapter = ExplorarOnlineAdapter(resultados) { articulo ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://es.wikipedia.org/?curid=${articulo.paginaId}")))
                }
                estado.text = if (resultados.isEmpty()) "No se han encontrado resultados. Prueba otra búsqueda."
                    else "${resultados.size} resultados · Toca uno para leer el artículo completo"
            } catch (e: Exception) {
                estado.text = "No se ha podido conectar. Comprueba tu conexión e inténtalo de nuevo."
                Toast.makeText(this@ExplorarOnlineActivity, "Error al consultar la biblioteca online", Toast.LENGTH_SHORT).show()
            } finally {
                progreso.visibility = View.GONE
                botonBuscar.isEnabled = true
            }
        }
    }

    companion object { const val EXTRA_CONSULTA = "consulta_online" }
}
