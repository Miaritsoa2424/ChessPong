package com.example;

public class Joueur {
    private String nom;
    private int point;

    public Joueur(String nom) {
        setNom(nom);
        setPoint(0);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void ajouterPoint(int points) {
        this.point += points;
    }

    public boolean isChampion() {
        return this.point >= 10; // Par exemple, un joueur est champion s'il atteint 10 points
    }

}