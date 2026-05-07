package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R

class AdminActivity : AppCompatActivity() {

    private lateinit var textTitre: TextView
    private lateinit var textUtilisateur: TextView

    private lateinit var boutonGestionUtilisateurs: TextView
    private lateinit var boutonGestionRoutine: TextView
    private lateinit var boutonGestionProduits: TextView

    private lateinit var boutonRetour: TextView

    private lateinit var prenomUtilisateur: String
    private lateinit var roleUtilisateur: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_hub)

        recupererDonneesIntent()
        verifierAccesAdmin()
        initialiserVues()
        afficherInformationsUtilisateur()
        initialiserListeners()
    }

    private fun recupererDonneesIntent() {
        prenomUtilisateur = intent.getStringExtra("prenom_utilisateur") ?: "Utilisateur"
        roleUtilisateur = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    private fun verifierAccesAdmin() {
        if (roleUtilisateur != "ADMIN") {
            Toast.makeText(this, "Accès réservé aux administrateurs", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initialiserVues() {
        textTitre = findViewById(R.id.admin)
        textUtilisateur = findViewById(R.id.utilisateurAdminHub)

        boutonGestionUtilisateurs = findViewById(R.id.gestionUtilisateur)
        boutonGestionRoutine = findViewById(R.id.btngestionRoutine)
        boutonGestionProduits = findViewById(R.id.btngestionProduit)

        boutonRetour = findViewById(R.id.buttonRetourAdmin)
    }

    private fun afficherInformationsUtilisateur() {
        textUtilisateur.text = "Connecté : $prenomUtilisateur"
    }

    private fun initialiserListeners() {
        boutonRetour.setOnClickListener {
            finish()
        }

        boutonGestionUtilisateurs.setOnClickListener {
            ouvrirGestionUtilisateurs()
        }

        boutonGestionRoutine.setOnClickListener {
            ouvrirGestionRoutine()
        }

        boutonGestionProduits.setOnClickListener {
            ouvrirGestionProduits()
        }
    }

    private fun ouvrirGestionUtilisateurs() {
        val intent = Intent(this, GestionUtilisateursActivity::class.java)
        intent.putExtra("prenom_utilisateur", prenomUtilisateur)
        intent.putExtra("role_utilisateur", roleUtilisateur)
        startActivity(intent)
    }

    private fun ouvrirGestionRoutine() {
        val intent = Intent(this, GestionTachesActivity::class.java)
        startActivity(intent)
    }

    private fun ouvrirGestionProduits() {
        val intent = Intent(this, GestionProduitsActivity::class.java)
        startActivity(intent)
    }
}