package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
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

    private lateinit var progressTaches: ProgressBar
    private lateinit var textPourcentageTaches: TextView
    private lateinit var textProgressionTaches: TextView
    private lateinit var textTachesRestantes: TextView

    /**
     * Initialise la base de données Room utilisée par l'application.
     */
    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Initialise les vues du dashboard.
     */
    private fun initialiserVues() {
        recyclerUtilisateurs = findViewById(R.id.recyclerUtilisateur)

        progressTaches = findViewById(R.id.progressTaches)
        textPourcentageTaches = findViewById(R.id.textPourcentageTaches)
        textProgressionTaches = findViewById(R.id.textProgressionTaches)
        textTachesRestantes = findViewById(R.id.textTachesRestantes)

        recyclerUtilisateurs.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Vérifie si au moins un utilisateur existe dans la base.
     *
     * Si aucun utilisateur n'existe, un administrateur par défaut est créé.
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
     * Affiche les utilisateurs dans le RecyclerView.
     */
    private fun afficherUtilisateurs(utilisateurs: List<UtilisateurEntity>) {

        val adapter = UtilisateurAdapter(utilisateurs) { utilisateur ->

            val intent = Intent(this, LoginActivity::class.java)

            intent.putExtra(
                "prenom_selectionne",
                utilisateur.prenom
            )

            startActivity(intent)
        }

        recyclerUtilisateurs.adapter = adapter
    }

    /**
     * Affiche les statistiques des tâches HACCP.
     */
    private fun afficherProgressionTaches() {
        Thread {
            val calendrierDebut = java.util.Calendar.getInstance()
            calendrierDebut.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendrierDebut.set(java.util.Calendar.MINUTE, 0)
            calendrierDebut.set(java.util.Calendar.SECOND, 0)
            calendrierDebut.set(java.util.Calendar.MILLISECOND, 0)

            val debutJour = calendrierDebut.timeInMillis

            val calendrierFin = java.util.Calendar.getInstance()
            calendrierFin.set(java.util.Calendar.HOUR_OF_DAY, 23)
            calendrierFin.set(java.util.Calendar.MINUTE, 59)
            calendrierFin.set(java.util.Calendar.SECOND, 59)
            calendrierFin.set(java.util.Calendar.MILLISECOND, 999)

            val finJour = calendrierFin.timeInMillis

            val total = database.tacheDao().getAllTaches().size
            val realisees = database.executionTacheDao()
                .getExecutionsDuJour(debutJour, finJour)
                .size

            val restantes = total - realisees

            val pourcentage = if (total > 0) {
                (realisees * 100) / total
            } else {
                0
            }

            runOnUiThread {
                progressTaches.progress = pourcentage
                textPourcentageTaches.text = "$pourcentage%"
                textProgressionTaches.text = "$realisees / $total tâches"
                textTachesRestantes.text = "$restantes restantes"
            }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_acceuil)

        initialiserVues()

        initialiserBaseDeDonnees()

        Thread {

            initialiserUtilisateurSiBesoin()

            val utilisateurs =
                database.utilisateurDao().getTousLesUtilisateurs()

            runOnUiThread {

                afficherUtilisateurs(utilisateurs)

                afficherProgressionTaches()
            }

        }.start()
    }

    override fun onResume() {
        super.onResume()

        if (::database.isInitialized) {
            afficherProgressionTaches()
        }
    }
}