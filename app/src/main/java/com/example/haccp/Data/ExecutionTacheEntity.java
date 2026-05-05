package com.example.haccp.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "execution_taches")
public class ExecutionTacheEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int tacheId;
    private String nomTache;
    private String utilisateur;
    private long timestampExecution;
    private String commentaire;
    private String valeur; // ex: "4°C" pour une température, null sinon

    public ExecutionTacheEntity(
            int tacheId,
            String nomTache,
            String utilisateur,
            long timestampExecution,
            String commentaire,
            String valeur
    ) {
        this.tacheId = tacheId;
        this.nomTache = nomTache;
        this.utilisateur = utilisateur;
        this.timestampExecution = timestampExecution;
        this.commentaire = commentaire;
        this.valeur = valeur;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getTacheId() { return tacheId; }

    public String getNomTache() { return nomTache; }

    public String getUtilisateur() { return utilisateur; }

    public long getTimestampExecution() { return timestampExecution; }

    public String getCommentaire() { return commentaire; }

    public String getValeur() { return valeur; }
}