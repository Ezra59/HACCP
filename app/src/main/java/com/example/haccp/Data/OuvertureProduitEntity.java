package com.example.haccp.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ouvertures_produits")
public class OuvertureProduitEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nomProduit;
    public String categorie;
    public int dureeApresOuverture;

    public String dateOuverture;
    public String dateLimiteConsommation;

    public String ouvertPar;
    public long timestampOuverture;

    public OuvertureProduitEntity(
            String nomProduit,
            String categorie,
            int dureeApresOuverture,
            String dateOuverture,
            String dateLimiteConsommation,
            String ouvertPar,
            long timestampOuverture
    ) {
        this.nomProduit = nomProduit;
        this.categorie = categorie;
        this.dureeApresOuverture = dureeApresOuverture;
        this.dateOuverture = dateOuverture;
        this.dateLimiteConsommation = dateLimiteConsommation;
        this.ouvertPar = ouvertPar;
        this.timestampOuverture = timestampOuverture;
    }
}