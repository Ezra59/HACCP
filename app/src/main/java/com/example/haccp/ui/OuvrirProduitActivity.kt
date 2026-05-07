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
import com.example.haccp.Data.OuvertureProduitEntity
import com.example.haccp.Data.ProduitReferenceEntity
import com.example.haccp.R
import com.example.haccp.adapter.ProduitReferenceAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class OuvrirProduitActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var database: AppDatabase
    private lateinit var textRetour: TextView

    private var utilisateur: String = "Inconnu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ouvrir_produit)

        initialiserVues()
        initialiserListeners()
        initialiserDatabase()
        recupererUtilisateur()
        chargerProduits()
    }

    private fun initialiserVues() {
        textRetour = findViewById(R.id.textRetour)
        recycler = findViewById(R.id.recyclerProduitsReference)
        recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun initialiserListeners() {
        textRetour.setOnClickListener {
            finish()
        }
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

    private fun recupererUtilisateur() {
        utilisateur = intent.getStringExtra("prenom_utilisateur") ?: "Inconnu"
    }

    private fun chargerProduits() {
        Thread {
            val produits = database.produitReferenceDao().getTousLesProduitsReference()

            runOnUiThread {
                recycler.adapter = ProduitReferenceAdapter(produits) { produit ->
                    ouvrirProduit(produit)
                }
            }
        }.start()
    }

    private fun ouvrirProduit(produit: ProduitReferenceEntity) {

        val dateOuvertureMillis = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateOuvertureMillis
        calendar.add(Calendar.DAY_OF_MONTH, produit.dureeApresOuverture)

        val dateLimiteMillis = calendar.timeInMillis

        val formatComplet = SimpleDateFormat("EEEE dd/MM/yyyy", Locale.FRENCH)
        val formatJour = SimpleDateFormat("EEEE", Locale.FRENCH)

        val dateOuverture = formatComplet.format(Date(dateOuvertureMillis))
            .replaceFirstChar { it.uppercase() }

        val dateLimite = formatComplet.format(Date(dateLimiteMillis))
            .replaceFirstChar { it.uppercase() }

        // JOUR DE PÉREMPTION
        val jourLimite = formatJour.format(Date(dateLimiteMillis))
            .uppercase(Locale.FRENCH)

        val vueEtiquette = layoutInflater.inflate(
            R.layout.dialog_etiquette_ouverture,
            null
        )

        val textJour = vueEtiquette.findViewById<TextView>(R.id.textJourEtiquette)
        val textProduit = vueEtiquette.findViewById<TextView>(R.id.textProduitEtiquette)
        val textCategorie = vueEtiquette.findViewById<TextView>(R.id.textCategorieEtiquette)
        val textDateOuverture = vueEtiquette.findViewById<TextView>(R.id.textDateOuvertureEtiquette)
        val textDateLimite = vueEtiquette.findViewById<TextView>(R.id.textDateLimiteEtiquette)
        val textDuree = vueEtiquette.findViewById<TextView>(R.id.textDureeEtiquette)
        val textUtilisateur = vueEtiquette.findViewById<TextView>(R.id.textUtilisateurEtiquette)

        textJour.text = jourLimite
        textProduit.text = "Produit : ${produit.nom}"
        textCategorie.text = "Catégorie : ${produit.categorie}"
        textDateOuverture.text = "Ouvert le : $dateOuverture"
        textDateLimite.text = "À consommer avant : $dateLimite"
        textDuree.text = "Durée après ouverture : ${produit.dureeApresOuverture} jour(s)"
        textUtilisateur.text = "Ouvert par : $utilisateur"

        AlertDialog.Builder(this)
            .setView(vueEtiquette)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Valider") { _, _ ->

                val ouverture = OuvertureProduitEntity(
                    produit.nom,
                    produit.categorie,
                    produit.dureeApresOuverture,
                    dateOuverture,
                    dateLimite,
                    utilisateur,
                    dateOuvertureMillis
                )

                enregistrerOuverture(ouverture)
            }
            .show()
    }

    private fun enregistrerOuverture(ouverture: OuvertureProduitEntity) {

        Thread {

            database.ouvertureProduitDao().insert(ouverture)

            runOnUiThread {

                Toast.makeText(
                    this,
                    "Produit ouvert avec succès",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.start()
    }
}