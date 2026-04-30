package com.example.haccp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haccp.Data.ProduitReferenceEntity
import com.example.haccp.R

class ProduitReferenceAdapter(
    private val produits: List<ProduitReferenceEntity>
) : RecyclerView.Adapter<ProduitReferenceAdapter.ProduitReferenceViewHolder>() {

    class ProduitReferenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNomProduit: TextView = itemView.findViewById(R.id.textNomProduit)
        val textCategorieProduit: TextView = itemView.findViewById(R.id.textCategorieProduit)
        val textDureeProduit: TextView = itemView.findViewById(R.id.textDureeProduit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProduitReferenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produit_reference, parent, false)

        return ProduitReferenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProduitReferenceViewHolder, position: Int) {
        val produit = produits[position]

        holder.textNomProduit.text = produit.nom
        holder.textCategorieProduit.text = "Catégorie : ${produit.categorie}"
        holder.textDureeProduit.text = "Durée après ouverture : ${produit.dureeApresOuverture} jour(s)"
    }

    override fun getItemCount(): Int {
        return produits.size
    }
}