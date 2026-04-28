package com.example.haccp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.haccp.Data.UtilisateurEntity
import com.example.haccp.R

class UtilisateurAdapter(
    private var listeUtilisateurs: List<UtilisateurEntity>,
    private val onUtilisateurClick: (UtilisateurEntity) -> Unit
) : RecyclerView.Adapter<UtilisateurAdapter.UtilisateurViewHolder>() {

    class UtilisateurViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buttonUtilisateur: Button = itemView.findViewById(R.id.buttonUtilisateur)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UtilisateurViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_utilisateur, parent, false)
        return UtilisateurViewHolder(view)
    }

    override fun onBindViewHolder(holder: UtilisateurViewHolder, position: Int) {
        val utilisateur = listeUtilisateurs[position]

        holder.buttonUtilisateur.text = utilisateur.prenom

        // 👉 UN SEUL POINT DE CLIC pour éviter les conflits
        holder.itemView.setOnClickListener {
            onUtilisateurClick(utilisateur)
        }

        // 👉 On force le bouton à déléguer le clic à la ligne
        holder.buttonUtilisateur.setOnClickListener {
            holder.itemView.performClick()
        }
    }

    override fun getItemCount(): Int = listeUtilisateurs.size

    fun updateList(nouvelleListe: List<UtilisateurEntity>) {
        listeUtilisateurs = nouvelleListe
        notifyDataSetChanged()
    }
}