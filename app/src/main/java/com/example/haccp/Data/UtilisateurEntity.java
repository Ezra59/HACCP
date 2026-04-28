package com.example.haccp.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "utilisateurs")
public class UtilisateurEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String prenom;
    private String codePin;
    private String role;

    public UtilisateurEntity(String prenom, String codePin, String role) {
        this.prenom = prenom;
        this.codePin = codePin;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getCodePin() {
        return codePin;
    }

    public String getRole() {
        return role;
    }
}