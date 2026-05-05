package com.example.haccp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haccp.Data.UtilisateurEntity
import com.example.haccp.R

class SupprimerUtilisateurAdapter(
    private var utilisateurs: List<UtilisateurEntity>,
    private val onUtilisateurClick: (UtilisateurEntity) -> Unit
) : RecyclerView.Adapter<SupprimerUtilisateurAdapter.UtilisateurViewHolder>() {

    class UtilisateurViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUtilisateur: TextView = itemView.findViewById(R.id.textUtilisateur)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UtilisateurViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_utilisateur, parent, false)

        return UtilisateurViewHolder(view)
    }

    override fun onBindViewHolder(holder: UtilisateurViewHolder, position: Int) {
        val utilisateur = utilisateurs[position]

        holder.textUtilisateur.text = utilisateur.prenom

        holder.itemView.setOnClickListener {
            onUtilisateurClick(utilisateur)
        }
    }

    override fun getItemCount(): Int {
        return utilisateurs.size
    }

    fun mettreAJourListe(nouvelleListe: List<UtilisateurEntity>) {
        utilisateurs = nouvelleListe
        notifyDataSetChanged()
    }
}