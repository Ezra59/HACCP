package com.example.haccp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
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

    private val categories = listOf(
        "Bof",
        "Charcuterie",
        "Dessert",
        "Épicerie",
        "Féculent",
        "Légumes",
        "Plat cuisiné",
        "Sauce",
        "Surgelé"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historique)

        initialiserVues()
        initialiserBaseDeDonnees()
        initialiserListeners()
        initialiserCategories()
        initialiserProduits()
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
     *
     * Permet notamment de revenir à l'écran précédent.
     */
    private fun initialiserListeners() {
        boutonRetour.setOnClickListener {
            finish()
        }
    }

    /**
     * Initialise le RecyclerView des catégories.
     *
     * Lorsqu'une catégorie est sélectionnée, les produits correspondants sont chargés.
     */
    private fun initialiserCategories() {
        recyclerCategories.layoutManager = GridLayoutManager(this, 9)

        val adapter = CategorieAdapter(categories) { categorie ->
            chargerProduitsParCategorie(categorie)
        }

        recyclerCategories.adapter = adapter
    }

    /**
     * Initialise le RecyclerView des produits.
     */
    private fun initialiserProduits() {
        historiqueProduit.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Charge les produits correspondant à une catégorie donnée.
     *
     * Les produits récupérés sont affichés dans le RecyclerView principal.
     * Un clic sur un produit ouvre l'écran de détail.
     *
     * @param categorie la catégorie sélectionnée
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
     *
     * Les informations du produit sont transmises via l'Intent.
     *
     * @param nom le nom du produit
     * @param lot le numéro de lot
     * @param date la date du produit
     * @param image l'URI de l'image
     * @param commentaire le commentaire associé au produit
     * @param dateReception la date de réception
     * @param enregistrePar le nom de l'utilisateur ayant enregistré le produit
     * @param categorie la catégorie du produit
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