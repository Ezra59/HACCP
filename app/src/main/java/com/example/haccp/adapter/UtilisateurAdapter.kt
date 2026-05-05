package com.example.haccp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haccp.Data.UtilisateurEntity
import com.example.haccp.R

class UtilisateurAdapter(
    private var listeUtilisateurs: List<UtilisateurEntity>,
    private val onUtilisateurClick: (UtilisateurEntity) -> Unit
) : RecyclerView.Adapter<UtilisateurAdapter.UtilisateurViewHolder>() {

    class UtilisateurViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUtilisateur: TextView = itemView.findViewById(R.id.textUtilisateur)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UtilisateurViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_utilisateur, parent, false)
        return UtilisateurViewHolder(view)
    }

    override fun onBindViewHolder(holder: UtilisateurViewHolder, position: Int) {
        val utilisateur = listeUtilisateurs[position]

        holder.textUtilisateur.text = utilisateur.prenom

        // 👉 toute la ligne est cliquable
        holder.itemView.setOnClickListener {
            onUtilisateurClick(utilisateur)
        }
    }

    override fun getItemCount(): Int = listeUtilisateurs.size

    fun updateList(nouvelleListe: List<UtilisateurEntity>) {
        listeUtilisateurs = nouvelleListe
        notifyDataSetChanged()
    }
}