package com.example.haccp.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "produits_reference")
public class ProduitReferenceEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nom;
    public String categorie;
    public int dureeApresOuverture;

    public ProduitReferenceEntity(String nom, String categorie, int dureeApresOuverture) {
        this.nom = nom;
        this.categorie = categorie;
        this.dureeApresOuverture = dureeApresOuverture;
    }
}