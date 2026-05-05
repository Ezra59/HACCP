package com.example.haccp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.ProduitReferenceEntity
import com.example.haccp.R
import com.example.haccp.adapter.ProduitReferenceAdapter

class GestionProduitsReferenceActivity : AppCompatActivity() {

    private lateinit var editNom: EditText
    private lateinit var editDuree: EditText
    private lateinit var spinnerCategorie: Spinner
    private lateinit var recyclerProduits: RecyclerView
    private lateinit var textRetour: TextView
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_produits_reference)

        initialiserVues()
        initialiserBaseDeDonnees()
        chargerCategories()
        chargerProduits()

        findViewById<Button>(R.id.btnAjouter).setOnClickListener {
            ajouterProduitReference()
        }
    }

    private fun initialiserVues() {
        editNom = findViewById(R.id.editNomProduit)
        editDuree = findViewById(R.id.editDuree)
        spinnerCategorie = findViewById(R.id.spinnerCategorie)
        recyclerProduits = findViewById(R.id.recyclerProduits)
        textRetour = findViewById(R.id.textRetour)

        recyclerProduits.layoutManager = LinearLayoutManager(this)

        textRetour.setOnClickListener {
            finish()
        }
    }

    private fun initialiserBaseDeDonnees() {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun chargerCategories() {
        val categories = db.categorieDao().getToutesLesCategories()

        if (categories.isEmpty()) {
            Toast.makeText(this, "Aucune catégorie disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val nomsCategories = categories.map { it.nom }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nomsCategories
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategorie.adapter = adapter
    }

    private fun ajouterProduitReference() {
        val nom = editNom.text.toString().trim()
        val dureeTexte = editDuree.text.toString().trim()

        if (nom.isEmpty() || dureeTexte.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (spinnerCategorie.selectedItem == null) {
            Toast.makeText(this, "Choisis une catégorie", Toast.LENGTH_SHORT).show()
            return
        }

        val duree = dureeTexte.toIntOrNull()

        if (duree == null || duree <= 0) {
            Toast.makeText(this, "La durée doit être un nombre positif", Toast.LENGTH_SHORT).show()
            return
        }

        val categorie = spinnerCategorie.selectedItem.toString()

        val produitReference = ProduitReferenceEntity(
            nom,
            categorie,
            duree
        )

        db.produitReferenceDao().insert(produitReference)

        Toast.makeText(this, "Produit ajouté", Toast.LENGTH_SHORT).show()

        editNom.text.clear()
        editDuree.text.clear()

        chargerProduits()
    }

    private fun chargerProduits() {
        val produits = db.produitReferenceDao().getTousLesProduitsReference()

        recyclerProduits.adapter = ProduitReferenceAdapter(produits) { produit ->
            editNom.setText(produit.nom)
            editDuree.setText(produit.dureeApresOuverture.toString())

            val adapter = spinnerCategorie.adapter
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i).toString() == produit.categorie) {
                    spinnerCategorie.setSelection(i)
                    break
                }
            }
        }
    }
}