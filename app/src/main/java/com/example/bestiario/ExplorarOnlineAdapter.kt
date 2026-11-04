package com.example.bestiario

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bestiario.data.ArticuloLeyenda

class ExplorarOnlineAdapter(
    private val articulos: List<ArticuloLeyenda>,
    private val alPulsar: (ArticuloLeyenda) -> Unit
) : RecyclerView.Adapter<ExplorarOnlineAdapter.ArticuloViewHolder>() {
    class ArticuloViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tvTituloArticulo)
        val resumen: TextView = view.findViewById(R.id.tvResumenArticulo)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fila_articulo_online, parent, false)
        return ArticuloViewHolder(view)
    }
    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        val articulo = articulos[position]
        holder.titulo.text = articulo.titulo
        @Suppress("DEPRECATION")
        holder.resumen.text = Html.fromHtml(articulo.resumenHtml, Html.FROM_HTML_MODE_COMPACT)
        holder.itemView.setOnClickListener { alPulsar(articulo) }
    }
    override fun getItemCount(): Int = articulos.size
}
