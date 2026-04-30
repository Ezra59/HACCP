package com.example.haccp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.R

class DeplacerProduitsCategorieActivity : AppCompatActivity() {

    private lateinit var textRetour: TextView
    private lateinit var spinnerCategorieSource: Spinner
    private lateinit var spinnerCategorieDestination: Spinner
    private lateinit var btnDeplacer: TextView
    private lateinit var database: AppDatabase

    private var categories: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deplacer_produits_categorie)

        initialiserVues()
        initialiserBaseDeDonnees()
        initialiserListeners()
        chargerCategories()
    }

    private fun initialiserVues() {
        textRetour = findViewById(R.id.textRetour)
        spinnerCategorieSource = findViewById(R.id.spinnerCategorieSource)
        spinnerCategorieDestination = findViewById(R.id.spinnerCategorieDestination)
        btnDeplacer = findViewById(R.id.btnDeplacer)
    }

    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).fallbackToDestructiveMigration().build()
    }

    private fun initialiserListeners() {
        textRetour.setOnClickListener {
            finish()
        }

        btnDeplacer.setOnClickListener {
            deplacerProduits()
        }
    }

    private fun chargerCategories() {
        Thread {
            categories = database.categorieDao()
                .getToutesLesCategories()
                .map { it.nom }
                .toMutableList()

            runOnUiThread {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    categories
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinnerCategorieSource.adapter = adapter
                spinnerCategorieDestination.adapter = adapter
            }
        }.start()
    }

    private fun deplacerProduits() {
        if (categories.size < 2) {
            Toast.makeText(
                this,
                "Il faut au moins deux catégories",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val source = spinnerCategorieSource.selectedItem.toString()
        val destination = spinnerCategorieDestination.selectedItem.toString()

        if (source == destination) {
            Toast.makeText(
                this,
                "Choisissez deux catégories différentes",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Thread {
            database.produitDao().modifierCategorieDesProduits(source, destination)

            runOnUiThread {
                Toast.makeText(
                    this,
                    "Produits déplacés vers $destination",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.start()
    }
}