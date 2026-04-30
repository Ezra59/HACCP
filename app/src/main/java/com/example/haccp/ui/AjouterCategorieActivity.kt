package com.example.haccp.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.CategorieEntity
import com.example.haccp.R

/**
 * Activity permettant d'ajouter une nouvelle catégorie.
 *
 * L'administrateur peut saisir un nom de catégorie,
 * qui sera enregistré en base de données.
 */
class AjouterCategorieActivity : AppCompatActivity() {

    private lateinit var champNomCategorie: EditText
    private lateinit var btnAjouter: TextView
    private lateinit var textRetour: TextView

    private lateinit var db: AppDatabase

    /**
     * Initialisation de l'écran
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_categorie)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).allowMainThreadQueries().build()

        initialiserVues()
        configurerActions()
    }

    /**
     * Récupération des composants graphiques
     */
    private fun initialiserVues() {
        champNomCategorie = findViewById(R.id.champNomCategorie)
        btnAjouter = findViewById(R.id.btnAjouter)
        textRetour = findViewById(R.id.textRetour)
    }

    /**
     * Gestion des interactions utilisateur
     */
    private fun configurerActions() {

        textRetour.setOnClickListener {
            finish()
        }

        btnAjouter.setOnClickListener {

            val nom = champNomCategorie.text.toString().trim()

            // Validation
            if (nom.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Vérifier si déjà existante
            val categorieExistante = db.categorieDao().trouverParNom(nom)

            if (categorieExistante != null) {
                Toast.makeText(this, "Cette catégorie existe déjà", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Création + insertion
            val categorie = CategorieEntity(nom)
            db.categorieDao().insert(categorie)

            Toast.makeText(this, "Catégorie ajoutée", Toast.LENGTH_SHORT).show()

            // Reset champ
            champNomCategorie.text.clear()
        }
    }
}