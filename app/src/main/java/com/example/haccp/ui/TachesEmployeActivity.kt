package com.example.haccp.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.ExecutionTacheEntity
import com.example.haccp.Data.TacheEntity
import com.example.haccp.R
import com.example.haccp.adapter.TacheAdapter

class TachesEmployeActivity : AppCompatActivity() {

    private lateinit var textRetour: TextView
    private lateinit var recyclerTaches: RecyclerView
    private lateinit var adapter: TacheAdapter
    private lateinit var database: AppDatabase

    private var utilisateurConnecte: String = "Utilisateur"

    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun initialiserVues() {
        textRetour = findViewById(R.id.textRetour)
        recyclerTaches = findViewById(R.id.recyclerTachesEmploye)

        adapter = TacheAdapter(emptyList()) { tache ->
            afficherValidationTache(tache)
        }

        recyclerTaches.layoutManager = LinearLayoutManager(this)
        recyclerTaches.adapter = adapter
    }

    private fun initialiserListeners() {
        textRetour.setOnClickListener {
            finish()
        }
    }

    private fun chargerUtilisateur() {
        utilisateurConnecte =
            intent.getStringExtra("utilisateur_connecte")
                ?: intent.getStringExtra("prenom_utilisateur")
                        ?: "Utilisateur"
    }

    private fun chargerTachesRestantes() {
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

            val toutesLesTaches = database.tacheDao().getAllTaches()
            val executionsDuJour = database.executionTacheDao()
                .getExecutionsDuJour(debutJour, finJour)

            val idsTachesFaitesAujourdhui = executionsDuJour.map { it.tacheId }

            val tachesRestantes = toutesLesTaches.filter { tache ->
                !idsTachesFaitesAujourdhui.contains(tache.id)
            }

            runOnUiThread {
                Toast.makeText(
                    this,
                    "Total: ${toutesLesTaches.size} | Faites aujourd'hui: ${executionsDuJour.size} | Restantes: ${tachesRestantes.size}",
                    Toast.LENGTH_LONG
                ).show()

                adapter.updateTaches(tachesRestantes)
            }
        }.start()
    }

    private fun afficherValidationTache(tache: TacheEntity) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 10)

        val champCommentaire = EditText(this)
        champCommentaire.hint = "Commentaire optionnel"

        val champValeur = EditText(this)
        champValeur.hint = "Valeur optionnelle, ex : 4°C"

        layout.addView(champCommentaire)
        layout.addView(champValeur)

        AlertDialog.Builder(this)
            .setTitle("Valider la tâche")
            .setMessage("Tâche : ${tache.titre}")
            .setView(layout)
            .setPositiveButton("Valider") { _, _ ->
                val commentaire = champCommentaire.text.toString().trim()
                val valeur = champValeur.text.toString().trim()

                enregistrerExecutionTache(
                    tache,
                    commentaire,
                    valeur
                )
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun enregistrerExecutionTache(
        tache: TacheEntity,
        commentaire: String,
        valeur: String
    ) {
        val execution = ExecutionTacheEntity(
            tache.id,
            tache.titre,
            utilisateurConnecte,
            System.currentTimeMillis(),
            commentaire,
            valeur
        )

        Thread {
            database.executionTacheDao().insert(execution)

            val executions = database.executionTacheDao().getAllExecutionTache()

            runOnUiThread {
                Toast.makeText(
                    this,
                    "Tâche validée. Total exécutions : ${executions.size}",
                    Toast.LENGTH_SHORT
                ).show()

                chargerTachesRestantes()
            }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taches_employe)

        chargerUtilisateur()
        initialiserBaseDeDonnees()
        initialiserVues()
        initialiserListeners()
        chargerTachesRestantes()
    }
}