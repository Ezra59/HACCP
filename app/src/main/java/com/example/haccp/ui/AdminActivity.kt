package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Activity représentant le hub administrateur de l'application.
 *
 * Cet écran est accessible uniquement aux utilisateurs ayant le rôle ADMIN.
 * Il permet d'accéder aux différentes sections de configuration :
 * - gestion des utilisateurs
 * - gestion des routines / catégories
 * - gestion des produits / règles
 */
class AdminActivity : AppCompatActivity() {

    private lateinit var textTitre: TextView
    private lateinit var textDate: TextView
    private lateinit var textUtilisateur: TextView

    private lateinit var boutonGestionUtilisateurs: TextView
    private lateinit var boutonGestionRoutine: TextView
    private lateinit var boutonGestionProduits: TextView

    private lateinit var boutonRetour: Button
    private lateinit var boutonDeconnexion: Button

    private lateinit var prenomUtilisateur: String
    private lateinit var roleUtilisateur: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_hub)

        recupererDonneesIntent()
        Toast.makeText(this, "Role reçu : $roleUtilisateur", Toast.LENGTH_SHORT).show()
        verifierAccesAdmin()
        initialiserVues()
        afficherInformationsUtilisateur()

        initialiserListeners()
    }

    /**
     * Récupère les informations utilisateur transmises par l'Intent.
     */
    private fun recupererDonneesIntent() {
        prenomUtilisateur = intent.getStringExtra("prenom_utilisateur") ?: "Utilisateur"
        roleUtilisateur = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    /**
     * Vérifie que l'utilisateur connecté possède bien le rôle ADMIN.
     *
     * Si ce n'est pas le cas, l'écran est fermé immédiatement.
     */
    private fun verifierAccesAdmin() {
        if (roleUtilisateur != "ADMIN") {
            Toast.makeText(this, "Accès réservé aux administrateurs", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private fun initialiserVues() {
        textTitre = findViewById(R.id.admin)
        textUtilisateur = findViewById(R.id.utilisateurAdminHub)

        boutonGestionUtilisateurs = findViewById(R.id.gestionUtilisateur)
        boutonGestionRoutine = findViewById(R.id.btngestionRoutine)
        boutonGestionProduits = findViewById(R.id.btngestionProduit)

        boutonRetour = findViewById(R.id.buttonRetourAdmin)
    }

    /**
     * Affiche les informations de l'utilisateur connecté.
     */
    private fun afficherInformationsUtilisateur() {
        textUtilisateur.text = "Connecté : $prenomUtilisateur"
    }



    /**
     * Initialise les interactions utilisateur sur l'écran admin.
     */
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



    /**
     * Ouvre l'écran de gestion des utilisateurs.
     *
     * Cette Activity pourra être implémentée plus tard.
     */
    private fun ouvrirGestionUtilisateurs() {
        val intent = Intent(this, GestionUtilisateursActivity::class.java)

        intent.putExtra("prenom_utilisateur", prenomUtilisateur)
        intent.putExtra("role_utilisateur", roleUtilisateur)

        startActivity(intent)
    }

    /**
     * Ouvre l'écran de gestion des routines / catégories.
     *
     * Cette Activity pourra être implémentée plus tard.
     */
    private fun ouvrirGestionRoutine() {
        Toast.makeText(this, "Gestion des routines à venir", Toast.LENGTH_SHORT).show()

        // Exemple futur :
        // val intent = Intent(this, GestionRoutineActivity::class.java)
        // startActivity(intent)
    }

    /**
     * Ouvre l'écran de gestion des produits / règles métier.
     *
     * Cette Activity pourra être implémentée plus tard.
     */
    private fun ouvrirGestionProduits() {
        Toast.makeText(this, "Gestion des produits à venir", Toast.LENGTH_SHORT).show()

        // Exemple futur :
        // val intent = Intent(this, GestionProduitsActivity::class.java)
        // startActivity(intent)
    }
}