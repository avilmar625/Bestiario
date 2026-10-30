package com.example.bestiario

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File

class CrearMonstruoActivity : ComponentActivity() {

    private lateinit var ivFotoMonstruo: ImageView
    private var fotoPath: String? = null
    private var archivoFotoActual: File? = null

    private val pedirPermisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) lanzarCamara()
        else Toast.makeText(this, "Sin permiso no puedo hacer la foto", Toast.LENGTH_SHORT).show()
    }

    private val hacerFoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        val archivo = archivoFotoActual
        if (exito && archivo != null) {
            fotoPath = archivo.absolutePath
            ivFotoMonstruo.setImageBitmap(Fotos.cargar(fotoPath))
        } else {
            archivo?.delete()
        }
    }

    private val elegirDeGaleria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val ruta = Fotos.copiarDesdeUri(this, uri)
            if (ruta != null) {
                fotoPath = ruta
                ivFotoMonstruo.setImageBitmap(Fotos.cargar(ruta))
            } else {
                Toast.makeText(this, "No se ha podido cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_monstruo)

        val etNombre = findViewById<EditText>(R.id.etNombreMonstruo)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcionMonstruo)
        ivFotoMonstruo = findViewById(R.id.ivFotoMonstruo)
        val dao = AppDatabase.obtener(this).monstruoUsuarioDao()

        findViewById<Button>(R.id.btnFotoMonstruo).setOnClickListener { pulsarBotonFoto() }

        findViewById<Button>(R.id.btnGaleriaMonstruo).setOnClickListener {
            elegirDeGaleria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        findViewById<Button>(R.id.btnGuardarMonstruo).setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            if (nombre.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Rellena el nombre y la descripción", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    dao.insertar(
                        MonstruoUsuario(
                            nombre = nombre,
                            descripcion = descripcion,
                            fotoPath = fotoPath
                        )
                    )
                    Toast.makeText(this@CrearMonstruoActivity, "Monstruo creado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        findViewById<Button>(R.id.btnVolverCrear).setOnClickListener { finish() }
    }

    private fun pulsarBotonFoto() {
        val permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permiso == PackageManager.PERMISSION_GRANTED) lanzarCamara()
        else pedirPermisoCamara.launch(Manifest.permission.CAMERA)
    }

    private fun lanzarCamara() {
        val archivo = Fotos.crearArchivo(this)
        archivoFotoActual = archivo
        hacerFoto.launch(Fotos.uriDe(this, archivo))
    }
}