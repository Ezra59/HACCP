package com.example.haccp.ui

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.haccp.R

/**
 * Activity permettant d'afficher les détails complets d'un produit.
 *
 * Cette classe reçoit les informations du produit via un Intent
 * et les affiche à l'écran (nom, lot, date, catégorie, etc.).
 */
class DetailProduitActivity : AppCompatActivity() {

    private lateinit var textNom: TextView
    private lateinit var textLot: TextView
    private lateinit var textDate: TextView
    private lateinit var textCategorie: TextView
    private lateinit var textCommentaire: TextView
    private lateinit var textDateReception: TextView
    private lateinit var textEnregistrePar: TextView
    private lateinit var imageProduit: ImageView
    private lateinit var boutonRetour: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produit)

        initialiserVues()
        initialiserListeners()
        afficherDonneesProduit()
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private fun initialiserVues() {
        textNom = findViewById(R.id.detailNom)
        textLot = findViewById(R.id.detailLot)
        textDate = findViewById(R.id.detailDate)
        textCategorie = findViewById(R.id.detailCategorie)
        textCommentaire = findViewById(R.id.detailCommentaire)
        textDateReception = findViewById(R.id.detailDateReception)
        textEnregistrePar = findViewById(R.id.detailEnregistrePar)
        imageProduit = findViewById(R.id.detailImage)
        boutonRetour = findViewById(R.id.textRetour)
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
     * Récupère les données envoyées via l'Intent et les affiche.
     */
    private fun afficherDonneesProduit() {

        val nom = intent.getStringExtra("nom")
        val lot = intent.getStringExtra("lot")
        val date = intent.getStringExtra("date")
        val categorie = intent.getStringExtra("categorie")
        val commentaire = intent.getStringExtra("commentaire")
        val dateReception = intent.getStringExtra("dateReception")
        val enregistrePar = intent.getStringExtra("enregistrePar")
        val image = intent.getStringExtra("image")

        textNom.text = nom ?: "Non renseigné"
        textLot.text = "Lot : ${lot ?: "Non renseigné"}"
        textDate.text = "Date : ${date ?: "Non renseigné"}"
        textCategorie.text = "Catégorie : ${categorie ?: "Non renseigné"}"
        textCommentaire.text = "Commentaire : ${commentaire ?: "Aucun"}"
        textDateReception.text = "Réception : ${dateReception ?: "Non renseigné"}"
        textEnregistrePar.text = "Enregistré par : ${enregistrePar ?: "Inconnu"}"

        afficherImage(image)
    }

    /**
     * Affiche l'image du produit si une URI est disponible.
     *
     * @param imageUriString l'URI de l'image sous forme de String
     */
    private fun afficherImage(imageUriString: String?) {
        if (!imageUriString.isNullOrEmpty()) {
            imageProduit.setImageURI(Uri.parse(imageUriString))
        }
    }
}