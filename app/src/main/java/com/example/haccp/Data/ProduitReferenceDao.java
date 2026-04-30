package com.example.haccp.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProduitReferenceDao {

    @Insert
    void insert(ProduitReferenceEntity produit);

    @Update
    void update(ProduitReferenceEntity produit);

    @Delete
    void delete(ProduitReferenceEntity produit);

    @Query("SELECT * FROM produits_reference ORDER BY nom ASC")
    List<ProduitReferenceEntity> getTousLesProduitsReference();

    @Query("SELECT * FROM produits_reference WHERE categorie = :categorie ORDER BY nom ASC")
    List<ProduitReferenceEntity> getProduitsReferenceParCategorie(String categorie);

    @Query("SELECT * FROM produits_reference WHERE id = :id LIMIT 1")
    ProduitReferenceEntity getProduitReferenceParId(int id);
}