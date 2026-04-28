package com.example.haccp.Model;

public class Produit {

    private String nom;
    private String lot;
    private String date;
    private String photoPath;
    private String commentaire;
    private String dateReception;
    private String enregistrePar;
    private String categorie;

    public Produit(String nom,
                   String lot,
                   String date,
                   String photoPath,
                   String commentaire,
                   String dateReception,
                   String enregistrePar,
                   String categorie) {

        this.nom = nom;
        this.lot = lot;
        this.date = date;
        this.photoPath = photoPath;
        this.commentaire = commentaire;
        this.dateReception = dateReception;
        this.enregistrePar = enregistrePar;
        this.categorie = categorie;
    }

    public String getNom() { return nom; }
    public String getLot() { return lot; }
    public String getDate() { return date; }
    public String getPhotoPath() { return photoPath; }
    public String getCommentaire() { return commentaire; }
    public String getDateReception() { return dateReception; }
    public String getEnregistrePar() { return enregistrePar; }
    public String getCategorie() { return categorie; }
}