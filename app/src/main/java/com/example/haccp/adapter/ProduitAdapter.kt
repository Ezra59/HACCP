package com.example.haccp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haccp.Data.ProduitEntity
import com.example.haccp.R

class ProduitAdapter(
    private val produits: List<ProduitEntity>,
    private val onClick: (ProduitEntity) -> Unit
) : RecyclerView.Adapter<ProduitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nom: TextView = view.findViewById(R.id.textNomProduit)
        val date: TextView = view.findViewById(R.id.textDateProduit)
        val lot: TextView = view.findViewById(R.id.textLotProduit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produithisto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produit = produits[position]

        holder.nom.text = produit.nom
        holder.date.text = "Date : ${produit.date}"
        holder.lot.text = "Lot : ${produit.numeroLot}"

        holder.itemView.setOnClickListener {
            onClick(produit)
        }
    }

    override fun getItemCount(): Int = produits.size
}