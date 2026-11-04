package com.example.bestiario

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class DetalleActivity : ComponentActivity() {
    private lateinit var reproductor: ReproductorMonstruo
    private lateinit var servicioUbicacion: ServicioUbicacion
    private lateinit var ivFoto: ImageView
    private lateinit var tvUbicacion: TextView
    private var latitud: Double? = null
    private var longitud: Double? = null
    private var fotoPath: String? = null
    private var archivoFotoActual: File? = null

    private val pedirPermisoCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) lanzarCamara() else Toast.makeText(this, "Sin permiso no puedo hacer la foto", Toast.LENGTH_SHORT).show()
    }
    private val hacerFoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val archivo = archivoFotoActual
        if (exito && archivo != null) { fotoPath = archivo.absolutePath; ivFoto.setImageBitmap(Fotos.cargar(fotoPath)) }
        else archivo?.delete()
    }
    private val pedirPermisosUbicacion = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        if (resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true || resultado[Manifest.permission.ACCESS_COARSE_LOCATION] == true) obtenerUbicacion()
        else Toast.makeText(this, "Sin permiso no puedo saber dónde estás", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)
        reproductor = ReproductorMonstruo(this)
        servicioUbicacion = ServicioUbicacion(this)
        ivFoto = findViewById(R.id.ivFoto)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        findViewById<Button>(R.id.btnFoto).setOnClickListener { pulsarBotonFoto() }
        findViewById<Button>(R.id.btnUbicacion).setOnClickListener { pulsarBotonUbicacion() }
        findViewById<Button>(R.id.botonVolverDetalle).setOnClickListener { finish() }

        val index = intent.getIntExtra(EXTRA_MONSTRUO_INDEX, -1)
        lifecycleScope.launch {
            val monstruo = Catalogo.todos(this@DetalleActivity).getOrNull(index)
            if (monstruo == null) {
                Toast.makeText(this@DetalleActivity, "No se ha encontrado esa criatura", Toast.LENGTH_SHORT).show()
                finish()
            } else mostrar(monstruo)
        }
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
    private fun pulsarBotonUbicacion() {
        val fina = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val aprox = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fina || aprox) obtenerUbicacion()
        else pedirPermisosUbicacion.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    private fun obtenerUbicacion() {
        servicioUbicacion.obtener(
            alObtener = { location ->
                if (!isFinishing && !isDestroyed) {
                    if (location == null) Toast.makeText(this, "No se ha podido obtener la ubicación", Toast.LENGTH_SHORT).show()
                    else {
                        latitud = location.latitude
                        longitud = location.longitude
                        tvUbicacion.text = "Ubicación: %.5f, %.5f".format(Locale.US, location.latitude, location.longitude)
                        tvUbicacion.setOnClickListener {
                            val uri = android.net.Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}")
                            startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    }
                }
            },
            alFallar = { if (!isFinishing && !isDestroyed) Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show() }
        )
    }

    private fun mostrar(monstruo: Monstruo) {
        val bitmap = Fotos.cargar(monstruo.fotoPath)
        val imagen = findViewById<ImageView>(R.id.imagenDetalle)
        if (bitmap != null) imagen.setImageBitmap(bitmap) else imagen.setImageResource(monstruo.imagenRes)
        findViewById<TextView>(R.id.nombreDetalle).text = monstruo.nombre
        findViewById<TextView>(R.id.descripcionDetalle).text = monstruo.descripcion
        reproductor.reproducir(monstruo.sonidoRes)

        val botonEditar = findViewById<Button>(R.id.btnEditarCriatura)
        if (monstruo.idUsuario != null) {
            botonEditar.visibility = android.view.View.VISIBLE
            botonEditar.setOnClickListener {
                startActivity(Intent(this, CrearMonstruoActivity::class.java).putExtra(CrearMonstruoActivity.EXTRA_ID, monstruo.idUsuario))
            }
        }

        val lugar = findViewById<EditText>(R.id.etLugar)
        val botonRegistrar = findViewById<Button>(R.id.btnRegistrar)
        botonRegistrar.setOnClickListener {
            val textoLugar = lugar.text.toString().trim()
            if (textoLugar.isBlank()) { lugar.error = "Indica dónde lo has visto"; return@setOnClickListener }
            botonRegistrar.isEnabled = false
            lifecycleScope.launch {
                try {
                    AppDatabase.obtener(this@DetalleActivity).avistamientoDao().insertar(
                        Avistamiento(monstruo = monstruo.nombre, lugar = textoLugar, fecha = System.currentTimeMillis(),
                            latitud = latitud, longitud = longitud, fotoPath = fotoPath)
                    )
                    Toast.makeText(this@DetalleActivity, "Avistamiento guardado", Toast.LENGTH_SHORT).show()
                    lugar.text.clear()
                    fotoPath = null; latitud = null; longitud = null; archivoFotoActual = null
                    ivFoto.setImageDrawable(null); tvUbicacion.text = "Ubicación: sin obtener"; tvUbicacion.setOnClickListener(null)
                } catch (e: Exception) {
                    Toast.makeText(this@DetalleActivity, "No se ha podido guardar el avistamiento", Toast.LENGTH_SHORT).show()
                } finally { botonRegistrar.isEnabled = true }
            }
        }

        val preferencias = getSharedPreferences("MisDatos", MODE_PRIVATE)
        val botonFavorito = findViewById<Button>(R.id.botonFavorito)
        fun actualizarBoton() {
            val favoritos = preferencias.getStringSet("FAVORITOS", emptySet()) ?: emptySet()
            botonFavorito.text = if (monstruo.nombre in favoritos) "★ En tus favoritos" else "☆ Añadir a favoritos"
        }
        actualizarBoton()
        botonFavorito.setOnClickListener {
            val favoritos = (preferencias.getStringSet("FAVORITOS", emptySet()) ?: emptySet()).toMutableSet()
            if (!favoritos.add(monstruo.nombre)) favoritos.remove(monstruo.nombre)
            preferencias.edit().putStringSet("FAVORITOS", favoritos).apply()
            actualizarBoton()
        }
    }

    override fun onDestroy() {
        reproductor.liberar()
        super.onDestroy()
    }
    companion object { const val EXTRA_MONSTRUO_INDEX = "MONSTRUO_INDEX" }
}
