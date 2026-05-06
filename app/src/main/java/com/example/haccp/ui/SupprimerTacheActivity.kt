package com.example.haccp.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.TacheEntity
import com.example.haccp.R
import com.example.haccp.adapter.TacheAdapter

class SupprimerTacheActivity : AppCompatActivity() {

    private lateinit var textRetour: TextView
    private lateinit var recyclerTaches: RecyclerView
    private lateinit var adapter: TacheAdapter
    private lateinit var database: AppDatabase

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
        recyclerTaches = findViewById(R.id.recyclerTaches)

        adapter = TacheAdapter(emptyList()) { tache ->
            afficherConfirmationSuppression(tache)
        }

        recyclerTaches.layoutManager = LinearLayoutManager(this)
        recyclerTaches.adapter = adapter
    }

    private fun initialiserListeners() {
        textRetour.setOnClickListener {
            finish()
        }
    }

    private fun chargerTaches() {
        Thread {
            val taches = database.tacheDao().getAllTaches()

            runOnUiThread {
                adapter.updateTaches(taches)
            }
        }.start()
    }

    private fun afficherConfirmationSuppression(tache: TacheEntity) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer la tâche")
            .setMessage("Voulez-vous vraiment supprimer la tâche : ${tache.titre} ?")
            .setPositiveButton("Supprimer") { _, _ ->
                supprimerTache(tache)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun supprimerTache(tache: TacheEntity) {
        Thread {
            database.tacheDao().supprimerTacheParId(tache.id)

            runOnUiThread {
                Toast.makeText(this, "Tâche supprimée", Toast.LENGTH_SHORT).show()
                chargerTaches()
            }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supprimer_tache)

        initialiserBaseDeDonnees()
        initialiserVues()
        initialiserListeners()
        chargerTaches()
    }
}