package com.example.haccp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.TacheEntity



class CreationTacheActivity : AppCompatActivity() {
    private lateinit var champNomTache : EditText
    private lateinit var  spinnerFrequence: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var buttonAjouterTache: Button
    private lateinit var retourTache: TextView

    private lateinit var database: AppDatabase

    val frequence = listOf("matin", "soir", "journee")

    //temporaire
    val type = listOf("nettoyage","temperature", "reception", "autre" )





    private fun initialiserVues() {
        champNomTache = findViewById(R.id.editNomTache)
        spinnerFrequence = findViewById(R.id.spinnerFrequenceTache)
        spinnerType = findViewById(R.id.spinnerTypeTache)
        buttonAjouterTache = findViewById(R.id.btnCreerTache)
        retourTache = findViewById(R.id.textRetour)
    }

    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        )
            //  DEV ONLY : supprime la base si le schéma change
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun initialiserSpinnerFrequence(){
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            frequence
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequence.adapter = adapter
    }

    private fun initialiserSpinnerType(){
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            type
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter
    }


    private fun initialiserListeners() {
        retourTache.setOnClickListener {
            finish()
        }
        buttonAjouterTache.setOnClickListener {
            val tache = recupChamps()

            if (tache.titre.isEmpty()) {
                champNomTache.error = "Nom obligatoire"
                return@setOnClickListener
            }

            Thread {
                database.tacheDao().insert(tache)

                runOnUiThread {
                    champNomTache.text.clear()
                }
            }.start()
        }
    }




    private fun recupChamps(): TacheEntity {
        return TacheEntity(
            champNomTache.text.toString().trim(),
            spinnerType.selectedItem.toString(),
            spinnerFrequence.selectedItem.toString()
        )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_tache)
        initialiserBaseDeDonnees()
        initialiserVues()
        initialiserSpinnerType()
        initialiserSpinnerFrequence()
        initialiserListeners()
    }



}