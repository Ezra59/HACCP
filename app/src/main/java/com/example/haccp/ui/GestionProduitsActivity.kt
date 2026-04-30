package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R


/**
 * Activity servant de hub pour la gestion des produits.
 *
 * Cette page permet à l'administrateur d'accéder aux différents modules :
 * - gestion des dates de péremption
 * - ajout de catégories
 * - modification de catégories
 * - suppression de catégories
 * - gestion des conditions de stockage
 */
class GestionProduitsActivity : AppCompatActivity() {

    private lateinit var textRetour: TextView
    private lateinit var btnDatesPeremption: TextView
    private lateinit var btnAjouterCategorie: TextView
    private lateinit var btnModifierCategorie: TextView
    private lateinit var btnSupprimerCategorie: TextView
    private lateinit var btnConditionsStockage: TextView
    private lateinit var btnDeplacerProduitsCategorie: TextView

    /**
     * Initialise l'écran et configure les actions des boutons du hub.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_produits)

        initialiserVues()
        configurerActions()
    }

    /**
     * Récupère les composants graphiques depuis le fichier XML.
     */
    private fun initialiserVues() {
        textRetour = findViewById(R.id.textRetour)
        btnDatesPeremption = findViewById(R.id.btnDatesPeremption)
        btnAjouterCategorie = findViewById(R.id.btnAjouterCategorie)
        btnModifierCategorie = findViewById(R.id.btnModifierCategorie)
        btnSupprimerCategorie = findViewById(R.id.btnSupprimerCategorie)
        btnConditionsStockage = findViewById(R.id.btnConditionsStockage)
        btnDeplacerProduitsCategorie= findViewById(R.id.btnDeplacerProduitsCategorie)
    }

    /**
     * Configure les clics sur les différents boutons du hub.
     */
    private fun configurerActions() {
        textRetour.setOnClickListener {
            finish()
        }

        btnDatesPeremption.setOnClickListener {
            ouvrirDatesPeremption()
        }

        btnAjouterCategorie.setOnClickListener {
           ouvrirAjouterCategorie()
        }

        btnModifierCategorie.setOnClickListener {
            ouvrirModifierCategorie()
        }

        btnSupprimerCategorie.setOnClickListener {
            ouvrirSupprimerCategorie()
        }

        btnConditionsStockage.setOnClickListener {
            afficherModuleEnPreparation()
        }
        btnDeplacerProduitsCategorie.setOnClickListener {
            ouvrirDeplacerCategorie()
        }
    }

    /**
     * Affiche un message temporaire pour les modules pas encore développés.
     */
    private fun afficherModuleEnPreparation() {
        Toast.makeText(
            this,
            "Module en préparation",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun ouvrirAjouterCategorie(){
        val intent = Intent(this, AjouterCategorieActivity::class.java)
        startActivity(intent)
    }

    private fun ouvrirSupprimerCategorie(){
        val intent = Intent(this, SupprimerCategorieActivity::class.java)
        startActivity(intent)
    }

    private fun ouvrirModifierCategorie(){
        val intent = Intent(this, ModifierCategorieActivity::class.java)
        startActivity(intent)
    }
    private fun ouvrirDeplacerCategorie(){
        val intent = Intent(this, DeplacerProduitsCategorieActivity::class.java)
        startActivity(intent)
    }

    private fun ouvrirDatesPeremption(){
        val intent = Intent(this, GestionProduitsReferenceActivity::class.java)
        startActivity(intent)
    }
}