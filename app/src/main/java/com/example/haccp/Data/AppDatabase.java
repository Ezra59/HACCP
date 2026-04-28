package com.example.haccp.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ProduitEntity.class, UtilisateurEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProduitDao produitDao();

    public abstract UtilisateurDao utilisateurDao();
}