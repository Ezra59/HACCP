package com.example.haccp.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.R
import com.example.haccp.adapter.CategorieAdapter
import android.widget.Spinner
import android.widget.ArrayAdapter

class SupprimerCategorieActivity : AppCompatActivity() {

    private lateinit var recyclerCategories: RecyclerView
    private lateinit var textRetour: TextView
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supprimer_categories)

        initialiserVues()
        initialiserBaseDeDonnees()
        initialiserListeners()
        chargerCategories()
    }

    private fun initialiserVues() {
        recyclerCategories = findViewById(R.id.recyclerCategories)
        textRetour = findViewById(R.id.textRetour)

        recyclerCategories.layoutManager = LinearLayoutManager(this)
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
    }

    private fun chargerCategories() {
        Thread {
            val categories = database.categorieDao()
                .getToutesLesCategories()
                .map { it.nom }

            runOnUiThread {
                val adapter = CategorieAdapter(categories) { categorie ->
                    confirmerSuppression(categorie)
                }

                recyclerCategories.adapter = adapter
            }
        }.start()
    }

    private fun confirmerSuppression(categorie: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Supprimer la catégorie \"$categorie\" ?")
            .setPositiveButton("Oui") { _, _ ->
                choisirCategorieDestination(categorie)
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun choisirCategorieDestination(categorieASupprimer: String) {
        Thread {
            val autresCategories = database.categorieDao()
                .getToutesLesCategories()
                .map { it.nom }
                .filter { it != categorieASupprimer }

            if (autresCategories.isEmpty()) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Impossible de supprimer : aucune autre catégorie disponible",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@Thread
            }

            runOnUiThread {

                val spinner = Spinner(this)

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    autresCategories
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                AlertDialog.Builder(this)
                    .setTitle("Déplacer les produits")
                    .setMessage("Choisissez une catégorie de destination")
                    .setView(spinner)
                    .setPositiveButton("Valider") { _, _ ->
                        val categorieDestination = spinner.selectedItem.toString()

                        supprimerCategorieAvecDeplacement(
                            categorieASupprimer,
                            categorieDestination
                        )
                    }
                    .setNegativeButton("Annuler", null)
                    .show()
            }
        }.start()
    }

    private fun supprimerCategorieAvecDeplacement(
        categorieASupprimer: String,
        categorieDestination: String
    ) {
        Thread {
            database.produitDao().modifierCategorieDesProduits(
                categorieASupprimer,
                categorieDestination
            )

            database.categorieDao().supprimerParNom(categorieASupprimer)

            runOnUiThread {
                Toast.makeText(
                    this,
                    "Catégorie supprimée, produits déplacés vers $categorieDestination",
                    Toast.LENGTH_LONG
                ).show()

                chargerCategories()
            }
        }.start()
    }
}