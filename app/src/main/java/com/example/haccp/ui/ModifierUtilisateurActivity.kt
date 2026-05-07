package com.example.haccp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.UtilisateurEntity
import com.example.haccp.R
import com.example.haccp.adapter.SupprimerUtilisateurAdapter

class ModifierUtilisateurActivity : AppCompatActivity() {

    private lateinit var recyclerUtilisateurs: RecyclerView
    private lateinit var champPrenom: EditText
    private lateinit var champPin: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var boutonModifier: Button
    private lateinit var texteRetour: TextView

    private lateinit var database: AppDatabase
    private lateinit var adapter: SupprimerUtilisateurAdapter

    private var utilisateurSelectionne: UtilisateurEntity? = null

    private var idUtilisateurConnecte: Int = -1
    private var roleUtilisateurConnecte: String = "Inconnu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_utilisateur)

        recupererUtilisateurConnecte()
        initialiserDatabase()
        initialiserVues()
        initialiserRecyclerView()
        initialiserListeners()
        chargerUtilisateurs()
    }

    private fun recupererUtilisateurConnecte() {
        idUtilisateurConnecte = intent.getIntExtra("id_utilisateur", -1)
        roleUtilisateurConnecte = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    private fun initialiserDatabase() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun initialiserVues() {
        recyclerUtilisateurs = findViewById(R.id.recyclerUtilisateurs)
        champPrenom = findViewById(R.id.editPrenom)
        champPin = findViewById(R.id.editPin)
        spinnerRole = findViewById(R.id.spinnerRole)
        boutonModifier = findViewById(R.id.buttonModifier)
        texteRetour = findViewById(R.id.textRetour)

        val roles = listOf("EMPLOYE", "ADMIN")

        spinnerRole.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )

        boutonModifier.isEnabled = false
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

        boutonModifier.setOnClickListener {
            modifierUtilisateur()
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

        champPrenom.setText(utilisateur.prenom)
        champPin.setText(utilisateur.codePin)

        val position = if (utilisateur.role == "ADMIN") 1 else 0
        spinnerRole.setSelection(position)

        boutonModifier.isEnabled = true
    }

    private fun modifierUtilisateur() {
        val utilisateur = utilisateurSelectionne ?: return

        val nouveauPrenom = champPrenom.text.toString().trim()
        val nouveauPin = champPin.text.toString().trim()
        val nouveauRole = spinnerRole.selectedItem.toString()

        if (nouveauPrenom.isEmpty() || nouveauPin.isEmpty()) {
            Toast.makeText(this, "Champs invalides", Toast.LENGTH_SHORT).show()
            return
        }

        if (nouveauPin.length != 6) {
            Toast.makeText(this, "Le PIN doit contenir 6 chiffres", Toast.LENGTH_SHORT).show()
            return
        }

        if (utilisateur.role == "ADMIN" && nouveauRole != "ADMIN") {
            verifierDernierAdminAvantModification(
                utilisateur,
                nouveauPrenom,
                nouveauPin,
                nouveauRole
            )
            return
        }

        enregistrerModification(
            utilisateur,
            nouveauPrenom,
            nouveauPin,
            nouveauRole
        )
    }

    private fun verifierDernierAdminAvantModification(
        utilisateur: UtilisateurEntity,
        nouveauPrenom: String,
        nouveauPin: String,
        nouveauRole: String
    ) {
        Thread {
            val utilisateurs = database.utilisateurDao().getAllUtilisateurs()
            val nombreAdmins = utilisateurs.count { it.role == "ADMIN" }

            runOnUiThread {
                if (nombreAdmins <= 1) {
                    Toast.makeText(
                        this,
                        "Impossible de retirer le rôle du dernier administrateur",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    enregistrerModification(
                        utilisateur,
                        nouveauPrenom,
                        nouveauPin,
                        nouveauRole
                    )
                }
            }
        }.start()
    }

    private fun enregistrerModification(
        utilisateur: UtilisateurEntity,
        nouveauPrenom: String,
        nouveauPin: String,
        nouveauRole: String
    ) {
        val utilisateurModifie = UtilisateurEntity(
            nouveauPrenom,
            nouveauPin,
            nouveauRole
        )

        utilisateurModifie.id = utilisateur.id

        Thread {
            database.utilisateurDao().update(utilisateurModifie)

            runOnUiThread {
                Toast.makeText(this, "Utilisateur modifié", Toast.LENGTH_SHORT).show()

                utilisateurSelectionne = utilisateurModifie
                chargerUtilisateurs()
            }
        }.start()
    }
}