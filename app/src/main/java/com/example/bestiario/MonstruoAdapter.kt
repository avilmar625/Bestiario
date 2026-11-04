package com.example.bestiario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MonstruoAdapter(
    private val monstruos: List<Monstruo>,
    private val onClickMonstruo: (Monstruo) -> Unit
) : RecyclerView.Adapter<MonstruoAdapter.MonstruoViewHolder>() {

    class MonstruoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.imagenFila)
        val nombre: TextView = view.findViewById(R.id.nombreFila)
        val descripcion: TextView = view.findViewById(R.id.descripcionFila)
        val tipo: TextView = view.findViewById(R.id.tipoFila)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonstruoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.fila_monstruo, parent, false)
        return MonstruoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MonstruoViewHolder, position: Int) {
        val monstruo = monstruos[position]
        holder.nombre.text = monstruo.nombre
        holder.descripcion.text = monstruo.descripcion
        holder.tipo.text = if (monstruo.sonidoRes != null) "LEYENDA ORIGINAL" else "CRIATURA CREADA"

        val bitmap = Fotos.cargar(monstruo.fotoPath)
        if (bitmap != null) holder.imagen.setImageBitmap(bitmap)
        else holder.imagen.setImageResource(monstruo.imagenRes)

        holder.itemView.setOnClickListener { onClickMonstruo(monstruo) }
    }

    override fun getItemCount(): Int = monstruos.size
}
