package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.R
import com.example.haccp.adapter.CategorieAdapter
import com.example.haccp.adapter.ProduitAdapter

/**
 * Activity permettant de consulter l'historique des produits enregistrés.
 *
 * L'utilisateur peut sélectionner une catégorie pour afficher les produits associés,
 * puis cliquer sur un produit afin d'accéder à son détail complet.
 */
class HistoriqueActivity : AppCompatActivity() {

    private lateinit var recyclerCategories: RecyclerView
    private lateinit var historiqueProduit: RecyclerView
    private lateinit var boutonRetour: TextView
    private lateinit var database: AppDatabase

    private lateinit var categorieAdapter: CategorieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historique)

        initialiserVues()
        initialiserBaseDeDonnees()
        initialiserListeners()
        initialiserCategories()
        initialiserProduits()
        chargerCategories()
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private fun initialiserVues() {
        recyclerCategories = findViewById(R.id.recyclerCategories)
        historiqueProduit = findViewById(R.id.historiqueProduit)
        boutonRetour = findViewById(R.id.textRetour)
    }

    /**
     * Initialise la base de données Room.
     */
    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Initialise les interactions utilisateur.
     */
    private fun initialiserListeners() {
        boutonRetour.setOnClickListener {
            finish()
        }
    }

    /**
     * Initialise le RecyclerView des catégories.
     */
    private fun initialiserCategories() {
        recyclerCategories.layoutManager = GridLayoutManager(this, 3)
    }

    /**
     * Charge les catégories enregistrées en base de données
     * et les affiche dans le RecyclerView.
     */
    private fun chargerCategories() {
        Thread {
            try {
                val categories = database.categorieDao()
                    .getToutesLesCategories()
                    .map { it.nom }

                runOnUiThread {
                    categorieAdapter = CategorieAdapter(categories) { categorie ->
                        chargerProduitsParCategorie(categorie)
                    }

                    recyclerCategories.adapter = categorieAdapter
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur chargement catégories : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    /**
     * Initialise le RecyclerView des produits.
     */
    private fun initialiserProduits() {
        historiqueProduit.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Charge les produits correspondant à une catégorie donnée.
     */
    private fun chargerProduitsParCategorie(categorie: String) {
        Thread {
            val produits = database.produitDao().getProduitsParCategorie(categorie)

            runOnUiThread {
                val adapter = ProduitAdapter(produits) { produit ->
                    ouvrirDetailProduit(
                        nom = produit.nom,
                        lot = produit.numeroLot,
                        date = produit.date,
                        image = produit.getPhotoUri(),
                        commentaire = produit.getCommentaire(),
                        dateReception = produit.getDateReception(),
                        enregistrePar = produit.getUtilisateur(),
                        categorie = produit.getCategorie()
                    )
                }

                historiqueProduit.adapter = adapter
            }
        }.start()
    }

    /**
     * Ouvre l'écran de détail d'un produit.
     */
    private fun ouvrirDetailProduit(
        nom: String?,
        lot: String?,
        date: String?,
        image: String?,
        commentaire: String?,
        dateReception: String?,
        enregistrePar: String?,
        categorie: String?
    ) {
        val intent = Intent(this, DetailProduitActivity::class.java)

        intent.putExtra("nom", nom)
        intent.putExtra("lot", lot)
        intent.putExtra("date", date)
        intent.putExtra("image", image)
        intent.putExtra("commentaire", commentaire)
        intent.putExtra("dateReception", dateReception)
        intent.putExtra("enregistrePar", enregistrePar)
        intent.putExtra("categorie", categorie)

        startActivity(intent)
    }
}