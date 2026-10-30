package com.example.bestiario

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.media.MediaPlayer
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.File

class DetalleActivity : ComponentActivity() {

    private var reproductor: MediaPlayer? = null
    private lateinit var ivFoto: ImageView
    private lateinit var tvUbicacion: TextView
    private lateinit var clienteUbicacion: FusedLocationProviderClient

    private var latitud: Double? = null
    private var longitud: Double? = null

    private var fotoPath: String? = null
    private var archivoFotoActual: File? = null

    // Cámara
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
            ivFoto.setImageBitmap(Fotos.cargar(fotoPath))
        } else {
            archivo?.delete()
        }
    }

    // Ubicación
    private val pedirPermisosUbicacion = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val fina = resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val aproximada = resultado[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fina || aproximada) obtenerUbicacion()
        else Toast.makeText(this, "Sin permiso no puedo saber dónde estás", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        ivFoto = findViewById(R.id.ivFoto)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.btnFoto).setOnClickListener { pulsarBotonFoto() }
        findViewById<Button>(R.id.btnUbicacion).setOnClickListener { pulsarBotonUbicacion() }

        val index = intent.getIntExtra("MONSTRUO_INDEX", 0)
        lifecycleScope.launch {
            val monstruo = Catalogo.todos(this@DetalleActivity)[index]
            mostrar(monstruo)
        }

        findViewById<Button>(R.id.botonVolverDetalle).setOnClickListener { finish() }
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

    private fun tienePermisoUbicacion(): Boolean {
        val fina = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val aproximada = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fina == PackageManager.PERMISSION_GRANTED ||
                aproximada == PackageManager.PERMISSION_GRANTED
    }

    private fun pulsarBotonUbicacion() {
        if (tienePermisoUbicacion()) {
            obtenerUbicacion()
        } else {
            pedirPermisosUbicacion.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        clienteUbicacion
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion: Location? ->
                if (ubicacion != null) {
                    latitud = ubicacion.latitude
                    longitud = ubicacion.longitude
                    tvUbicacion.text = "Ubicación: " + "%.4f".format(ubicacion.latitude) +
                            ", " + "%.4f".format(ubicacion.longitude)
                } else {
                    Toast.makeText(this, "No se ha podido obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun mostrar(monstruo: Monstruo) {
        val bitmapMonstruo = Fotos.cargar(monstruo.fotoPath)
        val imagenDetalle = findViewById<ImageView>(R.id.imagenDetalle)
        if (bitmapMonstruo != null) imagenDetalle.setImageBitmap(bitmapMonstruo)
        else imagenDetalle.setImageResource(monstruo.imagenRes)

        //findViewById<ImageView>(R.id.imagenDetalle).setImageResource(monstruo.imagenRes)
        findViewById<TextView>(R.id.nombreDetalle).text = monstruo.nombre
        findViewById<TextView>(R.id.descripcionDetalle).text = monstruo.descripcion

        reproductor = MediaPlayer.create(this, monstruo.sonidoRes)
        reproductor?.start()

        val etLugar = findViewById<EditText>(R.id.etLugar)
        val dao = AppDatabase.obtener(this).avistamientoDao()
        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val lugar = etLugar.text.toString().trim()
            if (lugar.isEmpty()) {
                Toast.makeText(this, "Escribe dónde lo has visto", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    dao.insertar(
                        Avistamiento(
                            monstruo = monstruo.nombre,
                            lugar = lugar,
                            fecha = System.currentTimeMillis(),
                            latitud = latitud,
                            longitud = longitud,
                            fotoPath = fotoPath
                        )
                    )
                    Toast.makeText(this@DetalleActivity, "Avistamiento guardado", Toast.LENGTH_SHORT).show()
                    etLugar.text.clear()
                    // limpiamos para que el siguiente avistamiento no reutilice esta foto ni esta ubicación
                    fotoPath = null
                    latitud = null
                    longitud = null
                    ivFoto.setImageDrawable(null)
                    tvUbicacion.text = "Ubicación: sin obtener"
                }
            }
        }

        val prefs = getSharedPreferences("MisDatos", MODE_PRIVATE)
        val botonFavorito = findViewById<Button>(R.id.botonFavorito)

        fun actualizarTextoBoton() {
            val favoritos = prefs.getStringSet("FAVORITOS", setOf()) ?: setOf()
            botonFavorito.text = if (favoritos.contains(monstruo.nombre))
                "Quitar de favoritos" else "Añadir a favoritos"
        }
        actualizarTextoBoton()

        botonFavorito.setOnClickListener {
            val favoritos = (prefs.getStringSet("FAVORITOS", setOf()) ?: setOf()).toMutableSet()
            if (favoritos.contains(monstruo.nombre)) favoritos.remove(monstruo.nombre)
            else favoritos.add(monstruo.nombre)
            prefs.edit().putStringSet("FAVORITOS", favoritos).apply()
            actualizarTextoBoton()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        reproductor?.release()
        reproductor = null
    }
}