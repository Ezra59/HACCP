package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.R
import com.example.haccp.adapter.TacheAdapter

class ModifierTacheActivity : AppCompatActivity() {

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
            val intent = Intent(this, ModifierTacheFormActivity::class.java)
            intent.putExtra("tacheId", tache.id)
            startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            chargerTaches()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_tache)

        initialiserBaseDeDonnees()
        initialiserVues()
        initialiserListeners()
        chargerTaches()
    }
}