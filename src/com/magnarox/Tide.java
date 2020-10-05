package com.magnarox;

import java.util.Date;

public class Tide {
    private Date date = null;
    private String hauteur = null;
    private String coef = null;
    private String bassePleine = null;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHauteur() {
        return hauteur;
    }

    public void setHauteur(String hauteur) {
        this.hauteur = hauteur;
    }

    public String getCoef() {
        return coef;
    }

    public void setCoef(String coef) {
        this.coef = coef;
    }

    public String getBassePleine() {
        return bassePleine;
    }

    public void setBassePleine(String bassePleine) {
        this.bassePleine = bassePleine;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if (getDate() != null) res.append("Date : " + getDate().toString());
        if (getHauteur() != null) res.append(" Hauteur : " + getHauteur());
        if (getCoef() != null) res.append(" Coeff : " + getCoef());
        if (getBassePleine() != null) res.append(" " + getBassePleine());
        return res.toString();
    }
}
