package com.example.haccp.Service;
import com.example.haccp.Data.TacheEntity;

public class TacheService {

    public TacheEntity creeTache(

            String titre,
            String type,
            String frequence){

        if (titre == null || titre.trim().isEmpty()){
            throw new IllegalArgumentException("Le nom de la tache est vide est vide");
        }
        if(type == null || type.trim().isEmpty()){
            throw  new IllegalArgumentException("le type de la tache est vide");
        }
        if(frequence == null || frequence.trim().isEmpty()){
            throw new IllegalArgumentException("la frequence de la tache est vide ");
        }


        return new TacheEntity(
                titre.trim(),
                type.trim(),
                frequence.trim());
    }
}
