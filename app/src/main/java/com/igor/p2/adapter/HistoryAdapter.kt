package com.igor.p2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.igor.p2.R
import com.igor.p2.model.Partida

class HistoryAdapter(private val partidas: List<Partida>) :
    RecyclerView.Adapter<HistoryAdapter.PartidaViewHolder>() {

    // ViewHolder Pattern
    class PartidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNome)
        val tvPontuacao: TextView = itemView.findViewById(R.id.tvPontuacao)
        val tvData: TextView = itemView.findViewById(R.id.tvData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_partida, parent, false)
        return PartidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartidaViewHolder, position: Int) {
        val partida = partidas[position]
        holder.tvNome.text = partida.nome
        holder.tvPontuacao.text = "Pontos: ${partida.pontuacao}"
        holder.tvData.text = partida.data
    }

    override fun getItemCount(): Int = partidas.size
}
