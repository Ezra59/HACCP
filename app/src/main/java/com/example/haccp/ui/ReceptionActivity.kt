package com.example.haccp.ui

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.haccp.Data.AppDatabase
import com.example.haccp.Data.ProduitEntity
import com.example.haccp.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream

class ReceptionActivity : AppCompatActivity() {

    private lateinit var imageProduit: ImageView
    private lateinit var champNom: EditText
    private lateinit var champLot: EditText
    private lateinit var champDate: EditText
    private lateinit var champCommentaire: EditText
    private lateinit var texteOCR: TextView
    private lateinit var boutonAjouter: Button
    private lateinit var boutonPhoto: Button
    private lateinit var spinnerCategorie: Spinner
    private lateinit var texteRetour: TextView

    private lateinit var database: AppDatabase

    private var imageUri: Uri? = null

    private var prenomUtilisateurConnecte: String = "Inconnu"
    private var roleUtilisateurConnecte: String = "Inconnu"

    data class DonneesProduit(
        val nom: String,
        val numeroLot: String,
        val date: String,
        val commentaire: String?,
        val categorie: String,
        val photoUri: String
    )

    private val categories = listOf(
        "Choisir une catégorie",
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

    private val prendrePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                imageProduit.setImageURI(imageUri)
                lancerOCRDepuisUri(imageUri!!)
            } else {
                Toast.makeText(this, "Prise de photo annulée", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialiserBaseDeDonnees()
        recupererUtilisateurConnecte()
        initialiserVues()
        initialiserSpinner()
        initialiserListeners()
    }

    private fun recupererUtilisateurConnecte() {
        prenomUtilisateurConnecte = intent.getStringExtra("prenom_utilisateur") ?: "Inconnu"
        roleUtilisateurConnecte = intent.getStringExtra("role_utilisateur") ?: "Inconnu"
    }

    private fun initialiserBaseDeDonnees() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "haccp_database"
        ).fallbackToDestructiveMigration().build()
    }

    private fun initialiserVues() {
        imageProduit = findViewById(R.id.photoproduit)
        champNom = findViewById(R.id.NomProduit)
        champLot = findViewById(R.id.NumeroLot)
        champDate = findViewById(R.id.DLC)
        champCommentaire = findViewById(R.id.commentaireAjouter)
        texteOCR = findViewById(R.id.testocr)
        boutonAjouter = findViewById(R.id.ButtonAjouter)
        boutonPhoto = findViewById(R.id.buttonphoto)
        spinnerCategorie = findViewById(R.id.categorieProd)
        texteRetour = findViewById(R.id.textRetour)
    }

    private fun initialiserSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategorie.adapter = adapter
    }

    private fun initialiserListeners() {
        texteRetour.setOnClickListener {
            finish()
        }

        boutonPhoto.setOnClickListener {
            imageUri = creerUriImageHD()

            if (imageUri != null) {
                prendrePhoto.launch(imageUri!!)
            } else {
                Toast.makeText(this, "Impossible de préparer la photo", Toast.LENGTH_SHORT).show()
            }
        }

        boutonAjouter.setOnClickListener {
            if (verifChamps()) {
                val donnees = recupChamps()
                val produit = creerProduit(donnees)
                enregistrerProduit(produit)
            }
        }
    }

    private fun verifChamps(): Boolean {
        if (champNom.text.toString().trim().isEmpty()) {
            champNom.error = "Nom obligatoire"
            return false
        }

        if (champLot.text.toString().trim().isEmpty()) {
            champLot.error = "Numéro de lot obligatoire"
            return false
        }

        if (champDate.text.toString().trim().isEmpty()) {
            champDate.error = "Date (DLC/DDM) obligatoire"
            return false
        }

        if (spinnerCategorie.selectedItemPosition == 0) {
            Toast.makeText(this, "Veuillez choisir une catégorie", Toast.LENGTH_SHORT).show()
            return false
        }

        if (imageUri == null) {
            Toast.makeText(this, "Veuillez prendre une photo", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun recupChamps(): DonneesProduit {
        return DonneesProduit(
            nom = champNom.text.toString().trim(),
            numeroLot = champLot.text.toString().trim(),
            date = champDate.text.toString().trim(),
            commentaire = champCommentaire.text.toString().trim().takeIf { it.isNotEmpty() },
            categorie = spinnerCategorie.selectedItem.toString(),
            photoUri = imageUri.toString()
        )
    }

    private fun creerProduit(donnees: DonneesProduit): ProduitEntity {
        return ProduitEntity(
            donnees.nom,
            donnees.numeroLot,
            donnees.date,
            donnees.photoUri,
            donnees.commentaire,
            System.currentTimeMillis().toString(),
            prenomUtilisateurConnecte,
            donnees.categorie
        )
    }

    private fun enregistrerProduit(produit: ProduitEntity) {
        Thread {
            try {
                database.produitDao().insert(produit)

                imageUri?.let { uri ->
                    sauvegarderCopiePhotoHACCP(
                        uri = uri,
                        nomProduit = produit.nom,
                        numeroLot = produit.numeroLot
                    )
                }

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Produit ajouté avec succès par $prenomUtilisateurConnecte",
                        Toast.LENGTH_SHORT
                    ).show()

                    viderFormulaire()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Erreur lors de l'enregistrement : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    private fun sauvegarderCopiePhotoHACCP(uri: Uri, nomProduit: String, numeroLot: String) {
        try {
            val dossier = File(getExternalFilesDir(null), "Photos_HACCP")

            if (!dossier.exists()) {
                dossier.mkdirs()
            }

            val nomProduitNettoye = nettoyerNomFichier(nomProduit)
            val lotNettoye = nettoyerNomFichier(numeroLot)
            val nomFichier = "${nomProduitNettoye}_${lotNettoye}_${System.currentTimeMillis()}.jpg"

            val fichierDestination = File(dossier, nomFichier)

            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(fichierDestination).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Log.d("HACCP_BACKUP", "Photo copiée : ${fichierDestination.absolutePath}")

        } catch (e: Exception) {
            Log.e("HACCP_BACKUP", "Erreur copie photo HACCP", e)
        }
    }

    private fun nettoyerNomFichier(texte: String): String {
        return texte
            .trim()
            .replace(" ", "_")
            .replace("/", "_")
            .replace("\\", "_")
            .replace(":", "_")
            .replace("*", "_")
            .replace("?", "_")
            .replace("\"", "_")
            .replace("<", "_")
            .replace(">", "_")
            .replace("|", "_")
    }

    private fun viderFormulaire() {
        champNom.text.clear()
        champLot.text.clear()
        champDate.text.clear()
        champCommentaire.text.clear()
        texteOCR.text = ""
        imageProduit.setImageDrawable(null)
        imageUri = null
        spinnerCategorie.setSelection(0)
    }

    private fun creerUriImageHD(): Uri? {
        val nomFichier = "HACCP_${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nomFichier)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/HACCP"
                )
            }
        }

        return try {
            contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        } catch (e: Exception) {
            Log.e("HACCP_IMAGE", "Erreur création Uri image HD", e)
            null
        }
    }

    private fun lancerOCRDepuisUri(uri: Uri) {
        try {
            val image = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val texteReconnu = visionText.text
                    texteOCR.text = texteReconnu

                    if (texteReconnu.isNotBlank()) {
                        extraireInfosDepuisOCR(texteReconnu)
                    } else {
                        Toast.makeText(this, "Aucune information détectée", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur OCR : ${e.message}", Toast.LENGTH_LONG).show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, "Erreur lecture image : ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun extraireInfosDepuisOCR(texte: String) {
        val texteNettoye = texte
            .replace("\n", " ")
            .replace("  ", " ")
            .trim()

        val regexDate = Regex(
            """\b(\d{1,2}[\/\-.]\d{1,2}[\/\-.](\d{2}|\d{4}))\b"""
        )

        val regexLot = Regex(
            """(?i)\b(?:lot|n[°o]?\s*lot|l|batch)\s*[:\-]?\s*([A-Z0-9][A-Z0-9\-\/]{2,})\b"""
        )

        val dateTrouvee = regexDate.find(texteNettoye)?.value
        val lotTrouve = regexLot.find(texteNettoye)?.groupValues?.getOrNull(1)

        if (champDate.text.toString().isBlank() && dateTrouvee != null) {
            champDate.setText(normaliserDate(dateTrouvee))
        }

        if (champLot.text.toString().isBlank() && lotTrouve != null) {
            champLot.setText(lotTrouve.uppercase())
        }

        if (dateTrouvee == null && lotTrouve == null) {
            Toast.makeText(
                this,
                "Aucune date ou lot détecté automatiquement",
                Toast.LENGTH_SHORT
            ).show()
        } else if (dateTrouvee == null || lotTrouve == null) {
            Toast.makeText(
                this,
                "OCR partiel : vérifiez les champs",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun normaliserDate(dateBrute: String): String {
        val morceaux = dateBrute
            .replace("-", "/")
            .replace(".", "/")
            .split("/")

        if (morceaux.size != 3) {
            return dateBrute
        }

        val jour = morceaux[0].padStart(2, '0')
        val mois = morceaux[1].padStart(2, '0')
        var annee = morceaux[2]

        if (annee.length == 2) {
            annee = "20$annee"
        }

        return "$jour/$mois/$annee"
    }
}