package com.togettech.ajarke.ui.agence.model;

import com.google.firebase.database.DatabaseReference;

public class Agence {
    private String id;
    private String agence_logo;
    private String agence_name;
    private String agence_place;
    private String agence_reseach;

    public Agence(String id, String agence_logo, String agence_name, String agence_place, String agence_reseach) {
        this.id = id;
        this.agence_logo = agence_logo;
        this.agence_name = agence_name;
        this.agence_place = agence_place;
        this.agence_reseach = agence_reseach;
    }

    public Agence(){

    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgence_logo() {
        return agence_logo;
    }

    public void setAgence_logo(String agence_logo) {
        this.agence_logo = agence_logo;
    }

    public String getAgence_name() {
        return agence_name;
    }

    public void setAgence_name(String agence_name) {
        this.agence_name = agence_name;
    }

    public String getAgence_place() {
        return agence_place;
    }

    public void setAgence_place(String agence_place) {
        this.agence_place = agence_place;
    }

    public String getAgence_reseach() {
        return agence_reseach;
    }

    public void setAgence_reseach(String agence_reseach) {
        this.agence_reseach = agence_reseach;
    }
}
