package com.example.bestiario

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recyclerMonstruos)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MonstruoAdapter(MonstruosData.lista) { monstruoPulsado ->
            val index = MonstruosData.lista.indexOf(monstruoPulsado)
            val intent = Intent(this, DetalleActivity::class.java)
            intent.putExtra("MONSTRUO_INDEX", index)
            startActivity(intent)
        }

        findViewById<Button>(R.id.botonFavoritos).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        findViewById<Button>(R.id.btnDiario).setOnClickListener {
            startActivity(Intent(this, AvistamientoActivity::class.java))
        }
    }
}