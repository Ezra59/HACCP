package com.example.haccp.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ProduitEntity.class, UtilisateurEntity.class, CategorieEntity.class, ProduitReferenceEntity.class, OuvertureProduitEntity.class, TacheEntity.class, ExecutionTacheEntity.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProduitDao produitDao();

    public abstract UtilisateurDao utilisateurDao();

    public abstract CategorieDao categorieDao();

    public abstract ProduitReferenceDao produitReferenceDao();
    public abstract OuvertureProduitDao ouvertureProduitDao();

    public abstract TacheDao tacheDao ();
    public abstract ExecutionTacheDao executionTacheDao();
}