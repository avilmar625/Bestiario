package com.example.bestiario

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class FavoritosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        val recycler = findViewById<RecyclerView>(R.id.recyclerFavoritos)
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.botonVolverFavoritos).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("MisDatos", MODE_PRIVATE)
        val nombresFavoritos = prefs.getStringSet("FAVORITOS", setOf()) ?: setOf()

        lifecycleScope.launch {
            val todos = Catalogo.todos(this@FavoritosActivity)
            val favoritos = todos.filter { it.nombre in nombresFavoritos }

            findViewById<RecyclerView>(R.id.recyclerFavoritos).adapter =
                MonstruoAdapter(favoritos) { monstruoPulsado ->
                    // el índice tiene que ser el de la lista COMPLETA, que es la que usa DetalleActivity
                    val index = todos.indexOf(monstruoPulsado)
                    val intent = Intent(this@FavoritosActivity, DetalleActivity::class.java)
                    intent.putExtra("MONSTRUO_INDEX", index)
                    startActivity(intent)
                }
        }
    }
}