package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R

class GestionTachesActivity : AppCompatActivity() {

    private lateinit var textRetour: TextView
    private lateinit var btnAjouterTache: Button
    private lateinit var btnModifierTache: Button
    private lateinit var btnSupprimerTache: Button

    private fun initialiserVues() {
        textRetour = findViewById(R.id.textRetour)
        btnAjouterTache = findViewById(R.id.btnAjouterTache)
        btnModifierTache = findViewById(R.id.btnModifierTache)
        btnSupprimerTache = findViewById(R.id.btnSupprimerTache)
    }

    private fun initialiserListeners() {
        textRetour.setOnClickListener {
            finish()
        }

        btnAjouterTache.setOnClickListener {
            val intent = Intent(this, CreationTacheActivity::class.java)
            startActivity(intent)
        }

        btnModifierTache.setOnClickListener {
            val intent = Intent(this, ModifierTacheActivity::class.java)
            startActivity(intent)
        }

        btnSupprimerTache.setOnClickListener {
            val intent = Intent(this, SupprimerTacheActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_taches)

        initialiserVues()
        initialiserListeners()
    }
}