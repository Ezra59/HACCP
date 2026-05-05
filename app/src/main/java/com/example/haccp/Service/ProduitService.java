package com.example.haccp.Service;

import com.example.haccp.Data.ProduitEntity;

public class ProduitService {

    public ProduitEntity creerProduit(String nom,
                                      String lot,
                                      String dlc,
                                      String photopath,
                                      String commentaire,
                                      String enregistrePar) {

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est vide");
        }

        if (lot == null || lot.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de lot est vide");
        }

        if (dlc == null || dlc.trim().isEmpty()) {
            throw new IllegalArgumentException("La DLC est vide");
        }

        return new ProduitEntity(
                nom.trim(),
                lot.trim(),
                dlc.trim(),
                photopath,
                commentaire,
                String.valueOf(System.currentTimeMillis()), // dateReception
                enregistrePar,
                "Non définie" // categorie
        );
    }
}