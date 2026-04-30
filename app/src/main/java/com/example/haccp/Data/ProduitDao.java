package com.example.haccp.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProduitDao {

    @Insert
    void insert(ProduitEntity produit);

    @Query("SELECT * FROM produits")
    List<ProduitEntity> getAllProduits();

    @Delete
    void delete(ProduitEntity produit);

    @Query("SELECT * FROM produits WHERE categorie = :categorie")
    List<ProduitEntity> getProduitsParCategorie(String categorie);

    @Query("UPDATE produits SET categorie = :nouveauNom WHERE categorie = :ancienNom")
    void modifierCategorieDesProduits(String ancienNom, String nouveauNom);


}