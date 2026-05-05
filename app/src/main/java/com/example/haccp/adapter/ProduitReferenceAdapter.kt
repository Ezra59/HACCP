package com.example.haccp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haccp.Data.ProduitReferenceEntity
import com.example.haccp.R

class ProduitReferenceAdapter(
    private var produits: List<ProduitReferenceEntity>,
    private val onClick: (ProduitReferenceEntity) -> Unit
) : RecyclerView.Adapter<ProduitReferenceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nom: TextView = itemView.findViewById(R.id.textNomProduitRef)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produit_reference, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produit = produits[position]

        holder.nom.text = "${produit.nom} - ${produit.dureeApresOuverture} jour(s)"

        holder.itemView.setOnClickListener {
            onClick(produit)
        }
    }

    override fun getItemCount(): Int = produits.size

    /**
     * Permet de mettre à jour la liste dynamiquement
     */
    fun updateList(nouvelleListe: List<ProduitReferenceEntity>) {
        produits = nouvelleListe
        notifyDataSetChanged()
    }
}