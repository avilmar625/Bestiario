package com.example.bestiario


import AppDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AvistamientoActivity : ComponentActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var tvContador: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avistamiento)

        recycler = findViewById(R.id.recyclerAvistamientos)
        tvContador = findViewById(R.id.tvContador)
        recycler.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.botonVolverAvistamientos).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        cargarLista()
    }

    private fun cargarLista() {
        val dao = AppDatabase.obtener(this).avistamientoDao()
        lifecycleScope.launch {
            val lista = dao.obtenerTodos()
            tvContador.text = "Avistamientos registrados: " + lista.size
            recycler.adapter = AvistamientoAdapter(lista) { avistamiento ->
                lifecycleScope.launch {
                    dao.borrar(avistamiento)
                    cargarLista()
                }
            }
        }
    }
}