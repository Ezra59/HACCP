package com.example.haccp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.UtilisateurEntity
import com.example.haccp.R

/**
 * Activity permettant à un administrateur de créer un nouvel utilisateur.
 *
 * L'écran permet de saisir :
 * - un prénom
 * - un code PIN
 * - un rôle via un menu déroulant
 *
 * Après validation, l'utilisateur est enregistré dans la base de données Room.
 */
class AjouterUtilisateurActivity : AppCompatActivity() {

    private lateinit var champNomUtilisateur: EditText
    private lateinit var champPinUtilisateur: EditText
    private lateinit var spinnerRole: Spinner

    private lateinit var boutonCreer: TextView
    private lateinit var boutonRetour: TextView

    private lateinit var database: AppDatabase

    private val roles = listOf(
        "Choisir un rôle",
        "ADMIN",
        "EMPLOYE"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_utilisateur)

        initialiserVues()
        initialiserBaseDeDonnees()
        initialiserSpinner()
        initialiserListeners()
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private fun initialiserVues() {
        champNomUtilisateur = findViewById(R.id.NomUtilisateur)
        champPinUtilisateur = findViewById(R.id.PinUtilisateur)
        spinnerRole = findViewById(R.id.spinner)

        boutonCreer = findViewById(R.id.buttonCreerUtilisateur)
        boutonRetour = findViewById(R.id.textRetour)
    }

    /**
     * Initialise la base de données Room.
     */
    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Initialise le menu déroulant des rôles.
     */
    private fun initialiserSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter
    }

    /**
     * Initialise les interactions utilisateur.
     */
    private fun initialiserListeners() {
        boutonRetour.setOnClickListener {
            finish()
        }

        boutonCreer.setOnClickListener {
            if (verifierChamps()) {
                creerUtilisateur()
            }
        }
    }

    /**
     * Vérifie la validité des champs du formulaire.
     *
     * Cette méthode contrôle :
     * - que le prénom n'est pas vide
     * - que le code PIN n'est pas vide
     * - que le code PIN contient exactement 6 chiffres
     * - qu'un rôle a été sélectionné
     *
     * @return true si les champs sont valides, false sinon
     */
    private fun verifierChamps(): Boolean {
        val nom = champNomUtilisateur.text.toString().trim()
        val pin = champPinUtilisateur.text.toString().trim()

        if (nom.isEmpty()) {
            champNomUtilisateur.error = "Nom obligatoire"
            return false
        }

        if (pin.isEmpty()) {
            champPinUtilisateur.error = "Code PIN obligatoire"
            return false
        }

        if (pin.length != 6 || !pin.all { it.isDigit() }) {
            champPinUtilisateur.error = "Le PIN doit contenir exactement 6 chiffres"
            return false
        }

        if (spinnerRole.selectedItemPosition == 0) {
            Toast.makeText(this, "Veuillez choisir un rôle", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    /**
     * Crée un nouvel utilisateur et l'enregistre dans la base de données.
     */
    private fun creerUtilisateur() {
        val nom = champNomUtilisateur.text.toString().trim()
        val pin = champPinUtilisateur.text.toString().trim()
        val role = spinnerRole.selectedItem.toString()

        Thread {
            try {
                val utilisateurExistant = database.utilisateurDao().trouverParPrenom(nom)

                runOnUiThread {
                    if (utilisateurExistant != null) {
                        champNomUtilisateur.error = "Ce prénom existe déjà"
                    } else {
                        val nouvelUtilisateur = UtilisateurEntity(
                            nom,
                            pin,
                            role
                        )

                        enregistrerUtilisateur(nouvelUtilisateur)
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur lors de la vérification : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    /**
     * Enregistre un utilisateur dans la base de données Room.
     *
     * @param utilisateur l'utilisateur à enregistrer
     */
    private fun enregistrerUtilisateur(utilisateur: UtilisateurEntity) {
        Thread {
            try {
                database.utilisateurDao().insert(utilisateur)

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Utilisateur créé avec succès",
                        Toast.LENGTH_SHORT
                    ).show()
                    viderFormulaire()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur lors de la création : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    /**
     * Réinitialise le formulaire après la création d'un utilisateur.
     */
    private fun viderFormulaire() {
        champNomUtilisateur.text.clear()
        champPinUtilisateur.text.clear()
        spinnerRole.setSelection(0)
    }
}