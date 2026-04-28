package com.example.haccp.ui

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.UtilisateurEntity
import com.example.haccp.R
import com.example.haccp.adapter.SupprimerUtilisateurAdapter

class SupprimerUtilisateurActivity : AppCompatActivity() {

    private lateinit var recyclerUtilisateurs: RecyclerView
    private lateinit var texteNomUtilisateur: TextView
    private lateinit var texteRoleUtilisateur: TextView
    private lateinit var boutonSupprimer: Button
    private lateinit var texteRetour: TextView

    private lateinit var database: AppDatabase
    private lateinit var adapter: SupprimerUtilisateurAdapter

    private var utilisateurSelectionne: UtilisateurEntity? = null

    private var idUtilisateurConnecte: Int = -1
    private var prenomUtilisateurConnecte: String = "Inconnu"
    private var roleUtilisateurConnecte: String = "Inconnu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supprimer_utilisateur)

        recupererUtilisateurConnecte()
        initialiserBaseDeDonnees()
        initialiserVues()
        initialiserRecyclerView()
        initialiserListeners()
        chargerUtilisateurs()
    }

    private fun recupererUtilisateurConnecte() {
        idUtilisateurConnecte = intent.getIntExtra("id_utilisateur", -1)
        prenomUtilisateurConnecte = intent.getStringExtra("prenom_utilisateur") ?: "Inconnu"
        roleUtilisateurConnecte = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).fallbackToDestructiveMigration().build()
    }

    private fun initialiserVues() {
        recyclerUtilisateurs = findViewById(R.id.recyclerUtilisateurs)
        texteNomUtilisateur = findViewById(R.id.textNomUtilisateur)
        texteRoleUtilisateur = findViewById(R.id.textRoleUtilisateur)
        boutonSupprimer = findViewById(R.id.buttonSupprimerUtilisateur)
        texteRetour = findViewById(R.id.textRetour)

        boutonSupprimer.isEnabled = false
    }

    private fun initialiserRecyclerView() {
        adapter = SupprimerUtilisateurAdapter(emptyList()) { utilisateur ->
            selectionnerUtilisateur(utilisateur)
        }

        recyclerUtilisateurs.layoutManager = LinearLayoutManager(this)
        recyclerUtilisateurs.adapter = adapter
    }

    private fun initialiserListeners() {
        texteRetour.setOnClickListener {
            finish()
        }

        boutonSupprimer.setOnClickListener {
            val utilisateur = utilisateurSelectionne

            if (utilisateur == null) {
                Toast.makeText(this, "Veuillez sélectionner un utilisateur", Toast.LENGTH_SHORT).show()
            } else {
                verifierAvantSuppression(utilisateur)
            }
        }
    }

    private fun chargerUtilisateurs() {
        Thread {
            val utilisateurs = database.utilisateurDao().getAllUtilisateurs()

            runOnUiThread {
                adapter.mettreAJourListe(utilisateurs)
            }
        }.start()
    }

    private fun selectionnerUtilisateur(utilisateur: UtilisateurEntity) {
        utilisateurSelectionne = utilisateur

        texteNomUtilisateur.text = "Nom : ${utilisateur.prenom}"
        texteRoleUtilisateur.text = "Rôle : ${utilisateur.role}"

        boutonSupprimer.isEnabled = true
    }

    private fun verifierAvantSuppression(utilisateur: UtilisateurEntity) {

        val memeId = utilisateur.id == idUtilisateurConnecte
        val memePrenom = utilisateur.prenom == prenomUtilisateurConnecte

        if (memeId || memePrenom) {
            Toast.makeText(
                this,
                "Vous ne pouvez pas supprimer votre propre compte",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (utilisateur.role.uppercase() == "ADMIN") {
            afficherPopupPinAdmin(utilisateur)
        } else {
            afficherPopupConfirmation(utilisateur)
        }
    }

    private fun afficherPopupPinAdmin(utilisateur: UtilisateurEntity) {
        val champPin = EditText(this).apply {
            hint = "Code PIN admin"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            maxLines = 1
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmation admin")
            .setMessage("Pour supprimer un administrateur, veuillez entrer votre PIN.")
            .setView(champPin)
            .setPositiveButton("Valider") { _, _ ->
                val pinSaisi = champPin.text.toString()
                verifierPinAdmin(pinSaisi, utilisateur)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun verifierPinAdmin(pinSaisi: String, utilisateur: UtilisateurEntity) {
        if (pinSaisi.isBlank()) {
            Toast.makeText(this, "Veuillez entrer votre PIN", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            val adminConnecte = database.utilisateurDao().trouverParPrenom(prenomUtilisateurConnecte)

            runOnUiThread {
                if (adminConnecte == null) {
                    Toast.makeText(
                        this,
                        "Impossible de retrouver l'administrateur connecté",
                        Toast.LENGTH_LONG
                    ).show()
                    return@runOnUiThread
                }

                if (adminConnecte.codePin != pinSaisi) {
                    Toast.makeText(this, "PIN incorrect", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }

                afficherPopupConfirmation(utilisateur)
            }
        }.start()
    }

    private fun afficherPopupConfirmation(utilisateur: UtilisateurEntity) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer utilisateur")
            .setMessage("Voulez-vous vraiment supprimer ${utilisateur.prenom} ?")
            .setPositiveButton("Supprimer") { _, _ ->
                supprimerUtilisateur(utilisateur)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun supprimerUtilisateur(utilisateur: UtilisateurEntity) {
        Thread {
            try {
                database.utilisateurDao().delete(utilisateur)

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Utilisateur supprimé",
                        Toast.LENGTH_SHORT
                    ).show()

                    utilisateurSelectionne = null
                    texteNomUtilisateur.text = "Veuillez sélectionner un utilisateur"
                    texteRoleUtilisateur.text = ""
                    boutonSupprimer.isEnabled = false

                    chargerUtilisateurs()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur suppression : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}