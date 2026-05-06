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
import com.example.haccp.Data.TacheEntity
import com.example.haccp.R

class ModifierTacheFormActivity : AppCompatActivity() {

    private lateinit var textRetour: TextView
    private lateinit var champNomTache: EditText
    private lateinit var spinnerFrequence: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var btnModifierTache: Button

    private lateinit var database: AppDatabase
    private var tacheId: Int = -1
    private var tacheActuelle: TacheEntity? = null

    private val frequences = listOf("matin", "soir", "journee")
    private val types = listOf("nettoyage", "temperature", "reception", "autre")

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
        champNomTache = findViewById(R.id.editNomTache)
        spinnerFrequence = findViewById(R.id.spinnerFrequenceTache)
        spinnerType = findViewById(R.id.spinnerTypeTache)
        btnModifierTache = findViewById(R.id.btnModifierTache)
    }

    private fun initialiserSpinners() {
        val adapterFrequence = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            frequences
        )
        adapterFrequence.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequence.adapter = adapterFrequence

        val adapterType = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            types
        )
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapterType
    }

    private fun chargerTache() {
        Thread {
            val tache = database.tacheDao().getTacheById(tacheId)

            runOnUiThread {
                tacheActuelle = tache
                remplirFormulaire(tache)
            }
        }.start()
    }

    private fun remplirFormulaire(tache: TacheEntity) {
        champNomTache.setText(tache.titre)

        val indexFrequence = frequences.indexOf(tache.frequence)
        if (indexFrequence >= 0) {
            spinnerFrequence.setSelection(indexFrequence)
        }

        val indexType = types.indexOf(tache.type)
        if (indexType >= 0) {
            spinnerType.setSelection(indexType)
        }
    }

    private fun initialiserListeners() {
        textRetour.setOnClickListener {
            finish()
        }

        btnModifierTache.setOnClickListener {
            modifierTache()
        }
    }

    private fun modifierTache() {
        val titre = champNomTache.text.toString().trim()

        if (titre.isEmpty()) {
            Toast.makeText(this, "Le nom de la tâche est obligatoire", Toast.LENGTH_SHORT).show()
            return
        }

        val tache = tacheActuelle ?: return

        tache.setTitre(titre)
        tache.setType(spinnerType.selectedItem.toString())
        tache.setFrequence(spinnerFrequence.selectedItem.toString())

        Thread {
            database.tacheDao().updateTache(tache)

            runOnUiThread {
                Toast.makeText(this, "Tâche modifiée", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier_tache_form)

        tacheId = intent.getIntExtra("tacheId", -1)

        if (tacheId == -1) {
            finish()
            return
        }

        initialiserBaseDeDonnees()
        initialiserVues()
        initialiserSpinners()
        initialiserListeners()
        chargerTache()
    }
}