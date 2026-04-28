package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.UtilisateurEntity
import com.example.haccp.R
import com.example.haccp.adapter.UtilisateurAdapter

class AccueilActivity : AppCompatActivity() {

    private lateinit var recyclerUtilisateurs: RecyclerView
    private lateinit var database: AppDatabase

    /**
     * Initialise la base de données Room utilisée par l'application.
     */
    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Vérifie si au moins un utilisateur existe dans la base.
     *
     * Si la table utilisateur est vide, cette méthode crée automatiquement
     * un compte administrateur par défaut afin de garantir l'accès initial
     * à l'application.
     */
    private fun initialiserUtilisateurSiBesoin() {
        val utilisateurs = database.utilisateurDao().getTousLesUtilisateurs()

        if (utilisateurs.isEmpty()) {
            val adminParDefaut = UtilisateurEntity(
                "Admin",
                "123456",
                "ADMIN"
            )

            database.utilisateurDao().insert(adminParDefaut)

            runOnUiThread {
                Toast.makeText(
                    this,
                    "Utilisateur par défaut créé : Admin / 123456",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Affiche la liste des utilisateurs dans le RecyclerView.
     *
     * Lorsqu'un utilisateur est sélectionné, son prénom est transmis
     * à LoginActivity pour poursuivre la connexion.
     *
     * @param utilisateurs la liste des utilisateurs à afficher
     */
    private fun afficherUtilisateurs(utilisateurs: List<UtilisateurEntity>) {
        val adapter = UtilisateurAdapter(utilisateurs) { utilisateur ->
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("prenom_selectionne", utilisateur.prenom)
            startActivity(intent)
        }

        recyclerUtilisateurs.adapter = adapter
    }

    /**
     * Initialise le RecyclerView affichant les utilisateurs.
     */
    private fun initialiserRecyclerView() {
        recyclerUtilisateurs = findViewById(R.id.recyclerUtilisateur)
        recyclerUtilisateurs.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceuil)

        initialiserRecyclerView()
        initialiserBaseDeDonnees()

        Thread {
            initialiserUtilisateurSiBesoin()
            val utilisateurs = database.utilisateurDao().getTousLesUtilisateurs()

            runOnUiThread {
                afficherUtilisateurs(utilisateurs)
            }
        }.start()
    }
}