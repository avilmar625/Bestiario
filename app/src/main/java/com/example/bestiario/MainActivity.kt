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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = mostrarLista()
            override fun afterTextChanged(s: Editable?) = Unit
        })
        findViewById<Button>(R.id.botonFavoritos).setOnClickListener { abrir(FavoritosActivity::class.java) }
        findViewById<Button>(R.id.btnDiario).setOnClickListener { abrir(AvistamientoActivity::class.java) }
        findViewById<Button>(R.id.btnCrearMonstruo).setOnClickListener { abrir(CrearMonstruoActivity::class.java) }
        findViewById<Button>(R.id.btnEstadisticas).setOnClickListener { abrir(EstadisticasActivity::class.java) }
        findViewById<Button>(R.id.btnAleatorio).setOnClickListener { abrirMonstruoAleatorio() }
        findViewById<Button>(R.id.btnExplorarOnline).setOnClickListener { abrir(ExplorarOnlineActivity::class.java) }
    }

    private fun abrir(clase: Class<*>) = startActivity(Intent(this, clase))

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

    private fun filtrar(): List<Monstruo> {
        val consulta = buscador.text.toString().trim().lowercase(Locale.getDefault())
        return monstruos.filter { monstruo ->
            val coincideTexto = monstruo.nombre.lowercase(Locale.getDefault()).contains(consulta) ||
                monstruo.descripcion.lowercase(Locale.getDefault()).contains(consulta)
            val coincideTipo = when (filtroActual) {
                Filtro.TODOS -> true
                Filtro.ORIGINALES -> monstruo.idUsuario == null
                Filtro.CREADOS -> monstruo.idUsuario != null
            }
            coincideTexto && coincideTipo
        }
    }

    private fun mostrarLista() {
        val filtrados = filtrar()
        tvContador.text = "${filtrados.size} criaturas"
        recycler.adapter = MonstruoAdapter(filtrados) { monstruo ->
            val indice = monstruos.indexOf(monstruo)
            if (indice >= 0) startActivity(Intent(this, DetalleActivity::class.java).putExtra(DetalleActivity.EXTRA_MONSTRUO_INDEX, indice))
        }
    }

    private fun abrirMonstruoAleatorio() {
        val elegido = filtrar().randomOrNull()
        if (elegido == null) return
        val indice = monstruos.indexOf(elegido)
        startActivity(Intent(this, DetalleActivity::class.java).putExtra(DetalleActivity.EXTRA_MONSTRUO_INDEX, indice))
    }
}
