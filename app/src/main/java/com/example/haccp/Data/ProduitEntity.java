package com.example.haccp.Data;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


    @Entity(tableName = "produits")
    public class ProduitEntity {

        @PrimaryKey(autoGenerate = true)
        private int id;

        private String nom;
        private String numeroLot;
        private String date;
        private String photoUri;
        private String commentaire;
        private String dateReception;
        private String utilisateur;
        private String categorie;

        public ProduitEntity(String nom, String numeroLot, String date, String photoUri,
                             String commentaire, String dateReception, String utilisateur, String categorie) {
            this.nom = nom;
            this.numeroLot = numeroLot;
            this.date = date;
            this.photoUri = photoUri;
            this.commentaire = commentaire;
            this.dateReception = dateReception;
            this.utilisateur = utilisateur;
            this.categorie = categorie;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }

        public String getNumeroLot() {
            return numeroLot;
        }

        public String getDate() {
            return date;
        }

        public String getPhotoUri() {
            return photoUri;
        }

        public String getCommentaire() {
            return commentaire;
        }

        public String getDateReception() {
            return dateReception;
        }

        public String getUtilisateur() {
            return utilisateur;
        }

        public String getCategorie() {
            return categorie;
        }
    }

