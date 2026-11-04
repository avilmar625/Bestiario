package com.example.bestiario

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bestiario.data.BestiarioRepository
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Pantalla principal: coordina la interfaz y delega la lectura de datos al repositorio.
 */
class MainActivity : ComponentActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var buscador: EditText
    private lateinit var tvContador: TextView
    private lateinit var repositorio: BestiarioRepository
    private var monstruos = emptyList<Monstruo>()
    private var filtroActual = Filtro.TODOS

    private enum class Filtro { TODOS, ORIGINALES, CREADOS }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repositorio = BestiarioRepository(this)
        recycler = findViewById(R.id.recyclerMonstruos)
        buscador = findViewById(R.id.etBuscarMonstruo)
        tvContador = findViewById(R.id.tvContadorCatalogo)
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnTodos).setOnClickListener { cambiarFiltro(Filtro.TODOS) }
        findViewById<Button>(R.id.btnOriginales).setOnClickListener { cambiarFiltro(Filtro.ORIGINALES) }
        findViewById<Button>(R.id.btnCreados).setOnClickListener { cambiarFiltro(Filtro.CREADOS) }

        buscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mostrarLista()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        findViewById<Button>(R.id.botonFavoritos).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        findViewById<Button>(R.id.btnDiario).setOnClickListener {
            startActivity(Intent(this, AvistamientoActivity::class.java))
        }
        findViewById<Button>(R.id.btnCrearMonstruo).setOnClickListener {
            startActivity(Intent(this, CrearMonstruoActivity::class.java))
        }
        findViewById<Button>(R.id.btnEstadisticas).setOnClickListener {
            startActivity(Intent(this, EstadisticasActivity::class.java))
        }
        findViewById<Button>(R.id.btnAleatorio).setOnClickListener { abrirMonstruoAleatorio() }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            monstruos = repositorio.obtenerMonstruos()
            mostrarLista()
        }
    }

    private fun cambiarFiltro(filtro: Filtro) {
        filtroActual = filtro
        mostrarLista()
    }

    private fun mostrarLista() {
        val consulta = buscador.text.toString().trim().lowercase(Locale.getDefault())
        val filtrados = monstruos.filter { monstruo ->
            val coincideTexto = monstruo.nombre.lowercase(Locale.getDefault()).contains(consulta) ||
                monstruo.descripcion.lowercase(Locale.getDefault()).contains(consulta)
            val coincideTipo = when (filtroActual) {
                Filtro.TODOS -> true
                Filtro.ORIGINALES -> monstruo.sonidoRes != null
                Filtro.CREADOS -> monstruo.sonidoRes == null
            }
            coincideTexto && coincideTipo
        }

        tvContador.text = "${filtrados.size} criaturas"
        recycler.adapter = MonstruoAdapter(filtrados) { monstruo ->
            abrirDetalle(monstruo)
        }
    }

    private fun abrirDetalle(monstruo: Monstruo) {
        val indice = monstruos.indexOf(monstruo)
        if (indice >= 0) {
            startActivity(Intent(this, DetalleActivity::class.java).apply {
                putExtra(DetalleActivity.EXTRA_MONSTRUO_INDEX, indice)
            })
        }
    }

    private fun abrirMonstruoAleatorio() {
        val consulta = buscador.text.toString().trim().lowercase(Locale.getDefault())
        val candidatos = monstruos.filter { monstruo ->
            val coincideTexto = monstruo.nombre.lowercase(Locale.getDefault()).contains(consulta) ||
                monstruo.descripcion.lowercase(Locale.getDefault()).contains(consulta)
            val coincideTipo = when (filtroActual) {
                Filtro.TODOS -> true
                Filtro.ORIGINALES -> monstruo.sonidoRes != null
                Filtro.CREADOS -> monstruo.sonidoRes == null
            }
            coincideTexto && coincideTipo
        }
        candidatos.randomOrNull()?.let { abrirDetalle(it) }
    }
}
