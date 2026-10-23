package com.example.bestiario

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        val prefs = getSharedPreferences("MisDatos", MODE_PRIVATE)
        val nombresFavoritos = prefs.getStringSet("FAVORITOS", setOf()) ?: setOf()
        val favoritos = MonstruosData.lista.filter { it.nombre in nombresFavoritos }

        val recycler = findViewById<RecyclerView>(R.id.recyclerFavoritos)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MonstruoAdapter(favoritos) { monstruoPulsado ->
            val index = MonstruosData.lista.indexOf(monstruoPulsado)
            val intent = Intent(this, DetalleActivity::class.java)
            intent.putExtra("MONSTRUO_INDEX", index)
            startActivity(intent)
        }

        findViewById<Button>(R.id.botonVolverFavoritos).setOnClickListener { finish() }
    }
}