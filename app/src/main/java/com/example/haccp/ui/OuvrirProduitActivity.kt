package com.example.haccp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.OuvertureProduitEntity
import com.example.haccp.Data.ProduitReferenceEntity
import com.example.haccp.R
import com.example.haccp.adapter.ProduitReferenceAdapter

import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import android.widget.TextView

class OuvrirProduitActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var database: AppDatabase
    private var utilisateur: String = "Inconnu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ouvrir_produit)

        utilisateur = intent.getStringExtra("prenom_utilisateur") ?: "Inconnu"

        recycler = findViewById(R.id.recyclerProduitsReference)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).fallbackToDestructiveMigration().build()

        chargerProduits()
    }

    private fun chargerProduits() {
        Thread {
            val produits = database.produitReferenceDao().getTousLesProduitsReference()

            runOnUiThread {
                recycler.layoutManager = LinearLayoutManager(this)
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

        val format = SimpleDateFormat("EEEE dd/MM/yyyy", Locale.FRENCH)

        val dateOuverture = format.format(Date(dateOuvertureMillis))
            .replaceFirstChar { it.uppercase() }

        val dateLimite = format.format(Date(dateLimiteMillis))
            .replaceFirstChar { it.uppercase() }

        val vueEtiquette = layoutInflater.inflate(R.layout.dialog_etiquette_ouverture, null)

        val textProduit = vueEtiquette.findViewById<TextView>(R.id.textProduitEtiquette)
        val textCategorie = vueEtiquette.findViewById<TextView>(R.id.textCategorieEtiquette)
        val textDateOuverture = vueEtiquette.findViewById<TextView>(R.id.textDateOuvertureEtiquette)
        val textDateLimite = vueEtiquette.findViewById<TextView>(R.id.textDateLimiteEtiquette)
        val textDuree = vueEtiquette.findViewById<TextView>(R.id.textDureeEtiquette)
        val textUtilisateur = vueEtiquette.findViewById<TextView>(R.id.textUtilisateurEtiquette)

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
                Toast.makeText(this, "Produit ouvert avec succès", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }
}
