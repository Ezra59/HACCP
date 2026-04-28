package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R

class GestionUtilisateursActivity : AppCompatActivity() {

    private lateinit var boutonAjouter: TextView
    private lateinit var boutonModifier: TextView
    private lateinit var boutonSupprimer: TextView
    private lateinit var boutonRetour: TextView

    private var idUtilisateurConnecte: Int = -1
    private var prenomUtilisateurConnecte: String = "Inconnu"
    private var roleUtilisateurConnecte: String = "Inconnu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_utilisateurs)

        recupererUtilisateurConnecte()
        initialiserVues()
        initialiserListeners()
    }

    private fun recupererUtilisateurConnecte() {
        idUtilisateurConnecte = intent.getIntExtra("id_utilisateur", -1)
        prenomUtilisateurConnecte = intent.getStringExtra("prenom_utilisateur") ?: "Inconnu"
        roleUtilisateurConnecte = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    private fun initialiserVues() {
        boutonAjouter = findViewById(R.id.ajouterutilisateur)
        boutonModifier = findViewById(R.id.modifierUtilisateur)
        boutonSupprimer = findViewById(R.id.supprimerUtilisateur)
        boutonRetour = findViewById(R.id.textRetour)
    }

    private fun initialiserListeners() {
        boutonAjouter.setOnClickListener {
            ouvrirAjouterUtilisateur()
        }

        boutonModifier.setOnClickListener {
            ouvrirModifierUtilisateur()
        }

        boutonSupprimer.setOnClickListener {
            ouvrirSupprimerUtilisateur()
        }

        boutonRetour.setOnClickListener {
            finish()
        }
    }

    private fun ouvrirAjouterUtilisateur() {
        val intent = Intent(this, AjouterUtilisateurActivity::class.java)

        intent.putExtra("id_utilisateur", idUtilisateurConnecte)
        intent.putExtra("prenom_utilisateur", prenomUtilisateurConnecte)
        intent.putExtra("role_utilisateur", roleUtilisateurConnecte)

        startActivity(intent)
    }

    private fun ouvrirModifierUtilisateur() {
        val intent = Intent(this, ModifierUtilisateurActivity::class.java)

        intent.putExtra("id_utilisateur", idUtilisateurConnecte)
        intent.putExtra("prenom_utilisateur", prenomUtilisateurConnecte)
        intent.putExtra("role_utilisateur", roleUtilisateurConnecte)

        startActivity(intent)
    }

    private fun ouvrirSupprimerUtilisateur() {
        val intent = Intent(this, SupprimerUtilisateurActivity::class.java)

        intent.putExtra("id_utilisateur", idUtilisateurConnecte)
        intent.putExtra("prenom_utilisateur", prenomUtilisateurConnecte)
        intent.putExtra("role_utilisateur", roleUtilisateurConnecte)

        startActivity(intent)
    }
}