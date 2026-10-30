package com.example.bestiario

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
        val a = lista[position]
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        val lat = a.latitud
        val lon = a.longitud
        val coordenadas = if (lat != null && lon != null) {
            "  (" + "%.4f".format(lat) + ", " + "%.4f".format(lon) + ")"
        } else {
            ""
        }

        holder.tvMonstruo.text = a.monstruo
        holder.tvLugar.text = "Visto en: " + a.lugar + coordenadas
        holder.tvFecha.text = formato.format(Date(a.fecha))
        val bitmap = Fotos.cargar(a.fotoPath)
        if (bitmap != null) {
            holder.ivFoto.setImageBitmap(bitmap)
            holder.ivFoto.visibility = View.VISIBLE
        } else {
            holder.ivFoto.visibility = View.GONE
        }
        holder.itemView.setOnLongClickListener {
            onBorrar(a)
            true
        }
    }

    override fun getItemCount(): Int = lista.size
}