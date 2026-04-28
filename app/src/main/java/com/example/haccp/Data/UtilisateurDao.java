package com.example.haccp.Data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import androidx.room.Delete;
import androidx.room.Update;


@Dao
public interface UtilisateurDao {

    @Insert
    void insert(UtilisateurEntity utilisateur);

    @Query("SELECT * FROM utilisateurs WHERE prenom = :prenom AND codePin = :codePin LIMIT 1")
    UtilisateurEntity connexion(String prenom, String codePin);

    @Query("SELECT * FROM utilisateurs WHERE prenom = :prenom LIMIT 1")
    UtilisateurEntity trouverParPrenom(String prenom);

    @Query("SELECT * FROM utilisateurs")
    List<UtilisateurEntity> getTousLesUtilisateurs();

    @Delete
    void delete(UtilisateurEntity utilisateur);

    @Query("SELECT * FROM utilisateurs")
    List<UtilisateurEntity> getAllUtilisateurs();

    @Update
    void update(UtilisateurEntity utilisateur);

}