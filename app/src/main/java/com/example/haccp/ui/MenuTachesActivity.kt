package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuTachesActivity : AppCompatActivity() {

    private lateinit var textDate: TextView
    private lateinit var textUtilisateur: TextView

    private lateinit var boutonReception: TextView
    private lateinit var boutonOuvrirProduit: TextView
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

    private fun recupererDonneesIntent() {
        prenomUtilisateur = intent.getStringExtra("prenom_utilisateur") ?: "Utilisateur"
        roleUtilisateur = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    private fun initialiserVues() {
        textDate = findViewById(R.id.datehub)
        textUtilisateur = findViewById(R.id.utilisateruhub)

        boutonReception = findViewById(R.id.btnReception)
        boutonOuvrirProduit = findViewById(R.id.btnOuvrirProduit)
        boutonHistorique = findViewById(R.id.btnHistorique)
        boutonAdmin = findViewById(R.id.btnAdmin)
        boutonDeconnexion = findViewById(R.id.buttonDeconnexion)
    }

    private fun afficherInformationsUtilisateur() {
        textUtilisateur.text = "Connecté : $prenomUtilisateur"
    }

    private fun afficherDate() {
        val formatter = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH)
        val dateActuelle = formatter.format(Date())

        textDate.text = dateActuelle.replaceFirstChar { it.uppercase() }
    }

    private fun configurerAccesAdmin() {
        if (roleUtilisateur != "ADMIN") {
            boutonAdmin.visibility = TextView.GONE
        }
    }

    private fun initialiserListeners() {
        boutonDeconnexion.setOnClickListener {
            deconnexion()
        }

        boutonReception.setOnClickListener {
            ouvrirReception()
        }

        boutonOuvrirProduit.setOnClickListener {
            ouvrirProduit()
        }

        boutonHistorique.setOnClickListener {
            ouvrirHistorique()
        }

        boutonAdmin.setOnClickListener {
            ouvrirAdmin()
        }
    }

    private fun deconnexion() {
        val intent = Intent(this, AccueilActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun ouvrirReception() {
        val intent = Intent(this, ReceptionActivity::class.java)
        intent.putExtra("prenom_utilisateur", prenomUtilisateur)
        intent.putExtra("role_utilisateur", roleUtilisateur)
        startActivity(intent)
    }

    private fun ouvrirProduit() {
        val intent = Intent(this, OuvrirProduitActivity::class.java)
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

    private fun ouvrirAdmin() {
        val intent = Intent(this, AdminActivity::class.java)
        intent.putExtra("prenom_utilisateur", prenomUtilisateur)
        intent.putExtra("role_utilisateur", roleUtilisateur)
        Toast.makeText(this, "Role envoyé : $roleUtilisateur", Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
}