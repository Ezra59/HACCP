package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
/**
 * Activity principale (hub) de l'application.
 *
 * Permet à l'utilisateur d'accéder aux différentes fonctionnalités :
 * - Réception produit
 * - Historique
 * - (Admin si autorisé)
 *
 * Affiche également les informations utilisateur et la date du jour.
 */
class MenuTachesActivity : AppCompatActivity() {

    private lateinit var textDate: TextView
    private lateinit var textUtilisateur: TextView

    private lateinit var boutonReception: TextView
    private lateinit var boutonHistorique: TextView
    private lateinit var boutonAdmin: TextView
    private lateinit var boutonDeconnexion: Button

    private lateinit var prenomUtilisateur: String
    private lateinit var roleUtilisateur: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)

        recupererDonneesIntent()
        initialiserVues()
        afficherInformationsUtilisateur()
        afficherDate()
        configurerAccesAdmin()
        initialiserListeners()
    }

    /**
     * Récupère les informations utilisateur depuis l'Intent.
     */
    private fun recupererDonneesIntent() {
        prenomUtilisateur = intent.getStringExtra("prenom_utilisateur") ?: "Utilisateur"
        roleUtilisateur = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private fun initialiserVues() {
        textDate = findViewById(R.id.datehub)
        textUtilisateur = findViewById(R.id.utilisateruhub)

        boutonReception = findViewById(R.id.btnReception)
        boutonHistorique = findViewById(R.id.btnHistorique)
        boutonAdmin = findViewById(R.id.btnAdmin) // ⚠️ à ajouter dans ton XML
        boutonDeconnexion = findViewById(R.id.buttonDeconnexion)
    }

    /**
     * Affiche les informations de l'utilisateur connecté.
     */
    private fun afficherInformationsUtilisateur() {
        textUtilisateur.text = "Connecté : $prenomUtilisateur"
    }

    /**
     * Affiche la date actuelle formatée.
     */
    private fun afficherDate() {
        val formatter = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH)
        val dateActuelle = formatter.format(Date())

        textDate.text = dateActuelle.replaceFirstChar { it.uppercase() }
    }

    /**
     * Configure l'accès à la section admin selon le rôle.
     *
     * Le bouton admin est visible uniquement pour les utilisateurs ADMIN.
     */
    private fun configurerAccesAdmin() {
        if (roleUtilisateur != "ADMIN") {
            boutonAdmin.visibility = TextView.GONE
        }
    }

    /**
     * Initialise les interactions utilisateur.
     */
    private fun initialiserListeners() {

        boutonDeconnexion.setOnClickListener {
            deconnexion()
        }

        boutonReception.setOnClickListener {
            ouvrirReception()
        }

        boutonHistorique.setOnClickListener {
            ouvrirHistorique()
        }

        boutonAdmin.setOnClickListener {
            ouvrirAdmin()
        }
    }

    /**
     * Gère la déconnexion de l'utilisateur.
     */
    private fun deconnexion() {
        val intent = Intent(this, AccueilActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Ouvre l'écran de réception produit.
     */
    private fun ouvrirReception() {
        val intent = Intent(this, ReceptionActivity::class.java)

        intent.putExtra("prenom_utilisateur", prenomUtilisateur)
        intent.putExtra("role_utilisateur", roleUtilisateur)

        startActivity(intent)
    }

    private fun ouvrirHistorique() {
        val intent = Intent(this, HistoriqueActivity::class.java)

        intent.putExtra("prenom_utilisateur", prenomUtilisateur)
        intent.putExtra("role_utilisateur", roleUtilisateur)

        startActivity(intent)
    }

    /**
     * Ouvre l'écran d'administration.
     *
     * Accessible uniquement aux utilisateurs ADMIN.
     */
    private fun ouvrirAdmin() {
        val intent = Intent(this, AdminActivity::class.java)
        intent.putExtra("prenom_utilisateur", prenomUtilisateur)
        intent.putExtra("role_utilisateur", roleUtilisateur)
        Toast.makeText(this, "Role envoyé : $roleUtilisateur", Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
}