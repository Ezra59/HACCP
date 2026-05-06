package com.example.haccp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haccp.Data.TacheEntity
import com.example.haccp.R

class TacheAdapter(
    private var taches: List<TacheEntity>,
    private val onClick: (TacheEntity) -> Unit
) : RecyclerView.Adapter<TacheAdapter.TacheViewHolder>() {

    class TacheViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitre: TextView = view.findViewById(R.id.textTitreTache)
        val textInfos: TextView = view.findViewById(R.id.textInfosTache)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TacheViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tache, parent, false)

        return TacheViewHolder(view)
    }

    override fun onBindViewHolder(holder: TacheViewHolder, position: Int) {
        val tache = taches[position]

        holder.textTitre.text = tache.titre
        holder.textInfos.text = "${tache.type} - ${tache.frequence}"

        holder.itemView.setOnClickListener {
            onClick(tache)
        }
    }

    override fun getItemCount(): Int {
        return taches.size
    }

    fun updateTaches(nouvellesTaches: List<TacheEntity>) {
        taches = nouvellesTaches
        notifyDataSetChanged()
    }
}