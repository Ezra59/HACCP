package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.R

/**
 * Activity de connexion par code PIN.
 *
 * L'utilisateur sélectionne son profil puis saisit un code PIN à 6 chiffres.
 * Si le code est correct, il est redirigé vers le menu principal.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var textPin: TextView
    private lateinit var textBienvenue: TextView

    private lateinit var buttonDelete: Button
    private lateinit var buttonRetour: Button

    private lateinit var database: AppDatabase
    private lateinit var prenomSelectionne: String

    private var pin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        recupererDonneesIntent()
        initialiserVues()
        initialiserBaseDeDonnees()
        initialiserListeners()
        mettreAJourAffichagePin()
    }

    /**
     * Récupère les données envoyées via l'Intent.
     */
    private fun recupererDonneesIntent() {
        prenomSelectionne = intent.getStringExtra("prenom_selectionne") ?: ""
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private fun initialiserVues() {
        textPin = findViewById(R.id.textPin)
        textBienvenue = findViewById(R.id.bienvenueMess)

        buttonDelete = findViewById(R.id.buttonDelete)
        buttonRetour = findViewById(R.id.buttonRetour)

        val prenom = prenomSelectionne.ifEmpty { "Utilisateur" }
        textBienvenue.text = "Bienvenue $prenom"
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
     * Initialise les interactions utilisateur.
     */
    private fun initialiserListeners() {

        // Boutons chiffres
        val boutons = listOf(
            R.id.button1 to "1",
            R.id.button2 to "2",
            R.id.button3 to "3",
            R.id.button4 to "4",
            R.id.button5 to "5",
            R.id.button6 to "6",
            R.id.button7 to "7",
            R.id.button8 to "8",
            R.id.button9 to "9",
            R.id.button0 to "0"
        )

        for ((id, chiffre) in boutons) {
            findViewById<Button>(id).setOnClickListener {
                ajouterChiffre(chiffre)
            }
        }

        buttonDelete.setOnClickListener {
            supprimerDernierChiffre()
        }

        buttonRetour.setOnClickListener {
            finish()
        }
    }

    /**
     * Ajoute un chiffre au code PIN.
     *
     * Lorsque 6 chiffres sont saisis, la vérification est automatiquement lancée.
     *
     * @param chiffre le chiffre à ajouter
     */
    private fun ajouterChiffre(chiffre: String) {
        if (pin.length < 6) {
            pin += chiffre
            mettreAJourAffichagePin()

            if (pin.length == 6) {
                verifierConnexion()
            }
        }
    }

    /**
     * Supprime le dernier chiffre du code PIN.
     */
    private fun supprimerDernierChiffre() {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            mettreAJourAffichagePin()
        }
    }

    /**
     * Met à jour l'affichage visuel du code PIN.
     *
     * Utilise des points pleins pour les chiffres saisis et des cercles vides pour les restants.
     */
    private fun mettreAJourAffichagePin() {
        val rempli = "● ".repeat(pin.length)
        val vide = "○ ".repeat(6 - pin.length)
        textPin.text = (rempli + vide).trim()
    }

    /**
     * Vérifie le code PIN dans la base de données.
     *
     * Si le code est correct, redirige vers le menu principal.
     * Sinon, réinitialise le code PIN.
     */
    private fun verifierConnexion() {
        Thread {
            try {
                val utilisateur =
                    database.utilisateurDao().connexion(prenomSelectionne, pin)

                runOnUiThread {
                    if (utilisateur != null) {
                        connexionReussie(utilisateur.prenom, utilisateur.role)
                    } else {
                        afficherErreurConnexion("Code PIN incorrect pour $prenomSelectionne")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    afficherErreurConnexion("Erreur connexion : ${e.message}")
                }
            }
        }.start()
    }

    /**
     * Gère une connexion réussie.
     *
     * @param prenom le prénom de l'utilisateur
     * @param role le rôle de l'utilisateur
     */
    private fun connexionReussie(prenom: String, role: String) {
        Toast.makeText(this, "Bienvenue $prenom", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MenuTachesActivity::class.java)
        intent.putExtra("prenom_utilisateur", prenom)
        intent.putExtra("role_utilisateur", role)

        startActivity(intent)
        finish()
    }

    /**
     * Affiche une erreur de connexion et réinitialise le code PIN.
     *
     * @param message le message d'erreur à afficher
     */
    private fun afficherErreurConnexion(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        pin = ""
        mettreAJourAffichagePin()
    }
}