package com.example.haccp.Data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategorieDao {

    @Insert
    void insert(CategorieEntity categorie);

    @Query("SELECT * FROM categories ORDER BY nom ASC")
    List<CategorieEntity> getToutesLesCategories();

    @Query("SELECT * FROM categories WHERE nom = :nom LIMIT 1")
    CategorieEntity trouverParNom(String nom);

    @Query("DELETE FROM categories WHERE nom = :nom")
    void supprimerParNom(String nom);

    @Query("UPDATE categories SET nom = :nouveauNom WHERE nom = :ancienNom")
    void modifierNomCategorie(String ancienNom, String nouveauNom);
}