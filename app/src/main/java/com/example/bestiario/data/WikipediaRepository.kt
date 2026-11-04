package com.example.bestiario.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class ArticuloLeyenda(val titulo: String, val resumenHtml: String, val paginaId: Long)

/** Consulta la API pública de Wikipedia en español sin bloquear el hilo principal. */
class WikipediaRepository {
    suspend fun buscar(consulta: String): List<ArticuloLeyenda> = withContext(Dispatchers.IO) {
        val texto = consulta.trim()
        require(texto.isNotEmpty()) { "Escribe qué quieres buscar" }
        val url = URL(
            "https://es.wikipedia.org/w/api.php?action=query&list=search" +
                "&srsearch=${URLEncoder.encode(texto, "UTF-8")}&format=json&utf8=1&srlimit=15"
        )
        val conexion = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 10000
            setRequestProperty("User-Agent", "BestiarioAndroid/1.0 (folklore explorer)")
        }
        try {
            val codigo = conexion.responseCode
            if (codigo !in 200..299) throw IllegalStateException("Wikipedia respondió con el código $codigo")
            val respuesta = conexion.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val resultados = JSONObject(respuesta).getJSONObject("query").getJSONArray("search")
            buildList {
                for (i in 0 until resultados.length()) {
                    val item = resultados.getJSONObject(i)
                    add(ArticuloLeyenda(item.optString("title"), item.optString("snippet"), item.optLong("pageid")))
                }
            }
        } finally {
            conexion.disconnect()
        }
    }
}
