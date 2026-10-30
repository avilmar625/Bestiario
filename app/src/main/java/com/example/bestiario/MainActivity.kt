package com.example.bestiario

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recyclerMonstruos)
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.botonFavoritos).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        findViewById<Button>(R.id.btnDiario).setOnClickListener {
            startActivity(Intent(this, AvistamientoActivity::class.java))
        }
        findViewById<Button>(R.id.btnCrearMonstruo).setOnClickListener {
            startActivity(Intent(this, CrearMonstruoActivity::class.java))
        }
    }

    // Se recarga cada vez que la pantalla vuelve a verse: así aparece el monstruo recién creado
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val lista = Catalogo.todos(this@MainActivity)
            recycler.adapter = MonstruoAdapter(lista) { monstruoPulsado ->
                val index = lista.indexOf(monstruoPulsado)
                val intent = Intent(this@MainActivity, DetalleActivity::class.java)
                intent.putExtra("MONSTRUO_INDEX", index)
                startActivity(intent)
            }
        }
    }
}