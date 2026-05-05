package com.example.haccp.Data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OuvertureProduitDao {

    @Insert
    void insert(OuvertureProduitEntity ouvertureProduit);

    @Query("SELECT * FROM ouvertures_produits ORDER BY timestampOuverture DESC")
    List<OuvertureProduitEntity> getToutesLesOuvertures();
}