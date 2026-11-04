package com.example.bestiario

import android.Manifest
import android.app.AlertDialog
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
import com.example.bestiario.data.BestiarioRepository
import kotlinx.coroutines.launch
import java.io.File

class CrearMonstruoActivity : ComponentActivity() {
    private lateinit var ivFotoMonstruo: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var repositorio: BestiarioRepository
    private var fotoPath: String? = null
    private var archivoFotoActual: File? = null
    private var idEdicion: Int? = null

    private val pedirPermisoCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) lanzarCamara() else Toast.makeText(this, "Sin permiso no puedo hacer la foto", Toast.LENGTH_SHORT).show()
    }
    private val hacerFoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val archivo = archivoFotoActual
        if (exito && archivo != null) {
            fotoPath = archivo.absolutePath
            ivFotoMonstruo.setImageBitmap(Fotos.cargar(fotoPath))
        } else archivo?.delete()
    }
    private val elegirDeGaleria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val ruta = Fotos.copiarDesdeUri(this, uri)
            if (ruta != null) { fotoPath = ruta; ivFotoMonstruo.setImageBitmap(Fotos.cargar(ruta)) }
            else Toast.makeText(this, "No se ha podido cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_monstruo)
        repositorio = BestiarioRepository(this)
        etNombre = findViewById(R.id.etNombreMonstruo)
        etDescripcion = findViewById(R.id.etDescripcionMonstruo)
        ivFotoMonstruo = findViewById(R.id.ivFotoMonstruo)
        idEdicion = intent.getIntExtra(EXTRA_ID, -1).takeIf { it > 0 }

        findViewById<Button>(R.id.btnFotoMonstruo).setOnClickListener { pulsarBotonFoto() }
        findViewById<Button>(R.id.btnGaleriaMonstruo).setOnClickListener {
            elegirDeGaleria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        findViewById<Button>(R.id.btnGuardarMonstruo).setOnClickListener { guardar() }
        findViewById<Button>(R.id.btnVolverCrear).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnEliminarMonstruo).apply {
            visibility = if (idEdicion != null) android.view.View.VISIBLE else android.view.View.GONE
            setOnClickListener { confirmarBorrado() }
        }

        idEdicion?.let { id ->
            lifecycleScope.launch {
                val criatura = repositorio.obtenerCriatura(id)
                if (criatura == null) { finish(); return@launch }
                etNombre.setText(criatura.nombre)
                etDescripcion.setText(criatura.descripcion)
                fotoPath = criatura.fotoPath
                ivFotoMonstruo.setImageBitmap(Fotos.cargar(fotoPath))
                findViewById<Button>(R.id.btnGuardarMonstruo).text = "Guardar cambios"
            }
        }
    }

    private fun guardar() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        if (nombre.isBlank()) { etNombre.error = "Escribe un nombre"; return }
        if (descripcion.isBlank()) { etDescripcion.error = "Añade una descripción"; return }
        val boton = findViewById<Button>(R.id.btnGuardarMonstruo)
        boton.isEnabled = false
        lifecycleScope.launch {
            try {
                val id = idEdicion ?: 0
                val anterior = if (id > 0) repositorio.obtenerCriatura(id) else null
                val criatura = MonstruoUsuario(id = id, nombre = nombre, descripcion = descripcion, fotoPath = fotoPath ?: anterior?.fotoPath)
                repositorio.guardarCriatura(criatura, idEdicion != null)
                Toast.makeText(this@CrearMonstruoActivity, if (idEdicion == null) "Criatura creada" else "Cambios guardados", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: IllegalArgumentException) {
                etNombre.error = e.message
            } catch (e: Exception) {
                Toast.makeText(this@CrearMonstruoActivity, "No se ha podido guardar. Inténtalo de nuevo.", Toast.LENGTH_LONG).show()
            } finally { boton.isEnabled = true }
        }
    }

    private fun confirmarBorrado() {
        val id = idEdicion ?: return
        AlertDialog.Builder(this)
            .setTitle("Eliminar criatura")
            .setMessage("Se eliminará esta criatura del catálogo. Los avistamientos anteriores se conservarán.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    val criatura = repositorio.obtenerCriatura(id)
                    if (criatura != null) AppDatabase.obtener(this@CrearMonstruoActivity).monstruoUsuarioDao().borrar(criatura)
                    Toast.makeText(this@CrearMonstruoActivity, "Criatura eliminada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.show()
    }

    private fun pulsarBotonFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) lanzarCamara()
        else pedirPermisoCamara.launch(Manifest.permission.CAMERA)
    }

    private fun lanzarCamara() {
        val archivo = Fotos.crearArchivo(this)
        archivoFotoActual = archivo
        hacerFoto.launch(Fotos.uriDe(this, archivo))
    }

    companion object { const val EXTRA_ID = "CRIATURA_ID" }
}
