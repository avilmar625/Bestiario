package com.example.bestiario

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

// Aísla la consulta GPS de la interfaz. La Activity debe comprobar y solicitar los permisos.
class ServicioUbicacion(context: Context) {

    private val cliente = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    @SuppressLint("MissingPermission")
    fun obtener(
        alObtener: (Location?) -> Unit,
        alFallar: () -> Unit
    ) {
        cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion -> alObtener(ubicacion) }
            .addOnFailureListener { alFallar() }
    }
}
