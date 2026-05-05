package com.example.haccp.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TacheDao {

    @Insert
    void insert(TacheEntity tache);

    @Query("SELECT * FROM taches")
    List<TacheEntity> getAllTaches();

    @Delete
    void delete(TacheEntity tache);

    @Query("SELECT * FROM taches WHERE type = :type")
    List<TacheEntity> getTacheParType(String type);

    @Query("SELECT * FROM taches WHERE frequence = :frequence")
    List<TacheEntity> getTacheParFrequence(String frequence);

    @Query("SELECT * FROM taches WHERE id = :id LIMIT 1")
    TacheEntity getTacheById(int id);
}
