package com.example.haccp.ui

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private val prendrePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                imageProduit.setImageURI(imageUri)
                rendreImageVisibleDansGalerie(imageUri!!)
                lancerOCRDepuisUri(imageUri!!)
            } else {
                Toast.makeText(this, "Prise de photo annulée", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        demanderPermissions()
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
        )
            // ⚠️ DEV ONLY : supprime la base si le schéma change
            .fallbackToDestructiveMigration()
            .build()
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
        Thread {
            try {
                val categories = database.categorieDao()
                    .getToutesLesCategories()
                    .map { it.nom }
                    .toMutableList()

                categories.add(0, "Choisir une catégorie")

                runOnUiThread {
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategorie.adapter = adapter
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

    private fun demanderPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 100)
        }
    }

    private fun creerUriImageHD(): Uri? {
        val nomFichier = "HACCP_${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nomFichier)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/HACCP"
                )
                put(MediaStore.Images.Media.IS_PENDING, 0)
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

    private fun rendreImageVisibleDansGalerie(uri: Uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                contentResolver.update(uri, values, null, null)
            }

            Toast.makeText(this, "Photo sauvegardée dans la galerie", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("HACCP_IMAGE", "Erreur visibilité galerie", e)
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
        val texteNettoye = nettoyerTexteOCR(texte)

        Log.d("HACCP_OCR", "Texte OCR nettoyé : $texteNettoye")

        val dateTrouvee = trouverDateDansTexte(texteNettoye)
        val lotTrouve = trouverLotDansTexte(texteNettoye)

        if (champDate.text.toString().isBlank() && dateTrouvee != null) {
            champDate.setText(dateTrouvee)
        }

        if (champLot.text.toString().isBlank() && lotTrouve != null) {
            champLot.setText(lotTrouve)
        }

        when {
            dateTrouvee == null && lotTrouve == null -> {
                Toast.makeText(
                    this,
                    "Aucune date ou lot détecté automatiquement",
                    Toast.LENGTH_SHORT
                ).show()
            }

            dateTrouvee == null || lotTrouve == null -> {
                Toast.makeText(
                    this,
                    "OCR partiel : vérifiez les champs",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Toast.makeText(
                    this,
                    "Date et lot détectés automatiquement",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun nettoyerTexteOCR(texte: String): String {
        return texte
            .replace("\n", " ")
            .replace("\r", " ")
            .replace(Regex("""(\d)[ ]+(\d)"""), "$1$2")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun trouverDateDansTexte(texte: String): String? {
        val texteCorrige = texte
            .replace("O", "0")
            .replace("o", "0")
            .replace("I", "1")
            .replace("l", "1")
            .replace("S", "5")
            .replace("s", "5")

        val regexAvecMotCle = Regex(
            """(?i)(?:date|dlc|ddm|exp|expiration|peremption|péremption)\s*[:\-]?\s*(\d{1,2}[\/\-.]\d{1,2}[\/\-.][0-9]{2,4})"""
        )

        val regexDateSimple = Regex(
            """\b(\d{1,2}[\/\-.]\d{1,2}[\/\-.][0-9]{2,4})\b"""
        )

        val dateAvecMotCle = regexAvecMotCle.find(texteCorrige)
            ?.groupValues
            ?.getOrNull(1)

        if (dateAvecMotCle != null) {
            return normaliserDate(dateAvecMotCle)
        }

        val dateSimple = regexDateSimple.find(texteCorrige)
            ?.groupValues
            ?.getOrNull(1)

        return dateSimple?.let { normaliserDate(it) }
    }

    private fun trouverLotDansTexte(texte: String): String? {
        val texteCorrige = texte
            .replace("N*Cde", "N Cde")
            .replace("N°", "N")
            .replace("n°", "n")

        val regexLotAvecMotCle = Regex(
            """(?i)\b(?:lot|n\s*lot|numéro\s*lot|numero\s*lot|batch|n/ref|ref|réf|reference|référence)\s*[:\-]?\s*([A-Z0-9][A-Z0-9\-\/]{2,})\b"""
        )

        val lotAvecMotCle = regexLotAvecMotCle.find(texteCorrige)
            ?.groupValues
            ?.getOrNull(1)

        if (lotAvecMotCle != null) {
            return nettoyerLot(lotAvecMotCle)
        }

        val regexLotProbable = Regex(
            """\b([A-Z]{1,3}[0-9]{3,}[A-Z0-9\-\/]*)\b"""
        )

        val lotProbable = regexLotProbable.find(texteCorrige)
            ?.groupValues
            ?.getOrNull(1)

        return lotProbable?.let { nettoyerLot(it) }
    }

    private fun nettoyerLot(lot: String): String {
        return lot
            .trim()
            .replace(" ", "")
            .replace("O", "0")
            .uppercase()
    }

    private fun normaliserDate(dateBrute: String): String {
        val dateCorrigee = dateBrute
            .replace("O", "0")
            .replace("o", "0")
            .replace("I", "1")
            .replace("l", "1")
            .replace("S", "5")
            .replace("s", "5")
            .replace("-", "/")
            .replace(".", "/")
            .trim()

        val morceaux = dateCorrigee.split("/")

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
}