package com.example.bestiario

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CrearMonstruoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_monstruo)

        val etNombre = findViewById<EditText>(R.id.etNombreMonstruo)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcionMonstruo)
        val dao = AppDatabase.obtener(this).monstruoUsuarioDao()

        findViewById<Button>(R.id.btnGuardarMonstruo).setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            if (nombre.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Rellena el nombre y la descripción", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    dao.insertar(MonstruoUsuario(nombre = nombre, descripcion = descripcion))
                    Toast.makeText(this@CrearMonstruoActivity, "Monstruo creado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        findViewById<Button>(R.id.btnVolverCrear).setOnClickListener { finish() }
    }
}