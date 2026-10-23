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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonstruoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.fila_monstruo, parent, false)
        return MonstruoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MonstruoViewHolder, position: Int) {
        val monstruo = monstruos[position]
        holder.nombre.text = monstruo.nombre
        holder.imagen.setImageResource(monstruo.imagenRes)

        holder.itemView.setOnClickListener {
            onClickMonstruo(monstruo)
        }
    }

    override fun getItemCount(): Int = monstruos.size
}