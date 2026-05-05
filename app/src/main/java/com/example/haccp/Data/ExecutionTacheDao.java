package com.example.haccp.Data;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface ExecutionTacheDao {


    @Insert
    void insert(ExecutionTacheEntity executionTache);

    @Delete
    void delete(ExecutionTacheEntity executionTache);

    @Query("SELECT * FROM execution_taches")
    List<ExecutionTacheEntity> getAllExecutionTache();

    @Query("SELECT * FROM execution_taches WHERE utilisateur = :utilisateur")
    List<ExecutionTacheEntity> getExecutionsParUtilisateur(String utilisateur);

    @Query("SELECT * FROM execution_taches WHERE tacheId = :tacheId")
    List<ExecutionTacheEntity> getExecutionsParTache(int tacheId);

    @Query("SELECT * FROM execution_taches ORDER BY timestampExecution DESC")
    List<ExecutionTacheEntity> getAllExecutionTacheTrie();

    @Query("SELECT * FROM execution_taches WHERE tacheId = :tacheId ORDER BY timestampExecution DESC")
    List<ExecutionTacheEntity> getExecutionsParTacheTrie(int tacheId);




}
