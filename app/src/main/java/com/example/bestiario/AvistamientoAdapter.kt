package com.example.bestiario

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvistamientoAdapter(
    private val lista: List<Avistamiento>,
    private val onBorrar: (Avistamiento) -> Unit
) : RecyclerView.Adapter<AvistamientoAdapter.ViewHolder>() {

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvMonstruo: TextView = vista.findViewById(R.id.tvMonstruoFila)
        val tvLugar: TextView = vista.findViewById(R.id.tvLugarFila)
        val tvFecha: TextView = vista.findViewById(R.id.tvFechaFila)
        val ivFoto: ImageView = vista.findViewById(R.id.ivFotoFila)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.fila_avistamiento, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val avistamiento = lista[position]
        val latitud = avistamiento.latitud
        val longitud = avistamiento.longitud

        val coordenadas = if (latitud != null && longitud != null) {
            "  (%.4f, %.4f)".format(Locale.US, latitud, longitud)
        } else {
            ""
        }

        holder.tvMonstruo.text = avistamiento.monstruo
        holder.tvLugar.text = "Visto en: ${avistamiento.lugar}$coordenadas"
        holder.tvFecha.text = formatoFecha.format(Date(avistamiento.fecha))

        val bitmap = Fotos.cargar(avistamiento.fotoPath)
        if (bitmap != null) {
            holder.ivFoto.setImageBitmap(bitmap)
            holder.ivFoto.visibility = View.VISIBLE
        } else {
            holder.ivFoto.setImageDrawable(null)
            holder.ivFoto.visibility = View.GONE
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Borrar avistamiento")
                .setMessage("¿Quieres eliminar este registro?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Borrar") { _, _ -> onBorrar(avistamiento) }
                .show()
            true
        }
    }

    override fun getItemCount(): Int = lista.size

    private companion object {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }
}
