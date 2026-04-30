package com.example.haccp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.R

class ModifierCategorieActivity : AppCompatActivity() {

    private lateinit var textRetour: TextView
    private lateinit var spinnerCategories: Spinner
    private lateinit var champNouveauNom: EditText
    private lateinit var btnModifier: TextView
    private lateinit var database: AppDatabase

    private var categories: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_categorie)

        initialiserVues()
        initialiserBaseDeDonnees()
        initialiserListeners()
        chargerCategories()
    }

    private fun initialiserVues() {
        textRetour = findViewById(R.id.textRetour)
        spinnerCategories = findViewById(R.id.spinnerCategories)
        champNouveauNom = findViewById(R.id.champNouveauNom)
        btnModifier = findViewById(R.id.btnModifier)
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

        btnModifier.setOnClickListener {
            modifierCategorie()
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
                spinnerCategories.adapter = adapter
            }
        }.start()
    }

    private fun modifierCategorie() {
        if (categories.isEmpty()) {
            Toast.makeText(this, "Aucune catégorie disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val ancienNom = spinnerCategories.selectedItem.toString()
        val nouveauNom = champNouveauNom.text.toString().trim()

        if (nouveauNom.isEmpty()) {
            champNouveauNom.error = "Nouveau nom obligatoire"
            return
        }

        if (ancienNom.equals(nouveauNom, ignoreCase = true)) {
            Toast.makeText(this, "Le nom est identique", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            val categorieExistante = database.categorieDao().trouverParNom(nouveauNom)

            if (categorieExistante != null) {
                runOnUiThread {
                    Toast.makeText(this, "Cette catégorie existe déjà", Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }

            database.categorieDao().modifierNomCategorie(ancienNom, nouveauNom)
            database.produitDao().modifierCategorieDesProduits(ancienNom, nouveauNom)

            runOnUiThread {
                Toast.makeText(this, "Catégorie modifiée", Toast.LENGTH_SHORT).show()
                champNouveauNom.text.clear()
                chargerCategories()
            }
        }.start()
    }
}