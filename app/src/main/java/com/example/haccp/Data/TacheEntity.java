package com.example.haccp.Data;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "taches")
public class TacheEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String titre;

    private String type;

    private String frequence; // MATIN, SOIR, JOURNEE

    public TacheEntity(String titre, String type, String frequence) {
        this.titre = titre;
        this.type = type;
        this.frequence = frequence;
    }

    public int getId(){return id;}
    public String getTitre(){return titre;}
    public String getType(){return type;}
    public String getFrequence(){return frequence;}

    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }
}

