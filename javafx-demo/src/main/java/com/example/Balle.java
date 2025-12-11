package com.example;

public class Balle {
    private double x, y; // Changé de int à double
    private int rayon;
    private double vitesseX;
    private double vitesseY;
    private double vitesse; // vitesse constante

    public Balle(int x, int y, int rayon, double vitesse, double angle) {
        setX(x);
        setY(y);
        setRayon(rayon);
        setVitesse(vitesse);
        setVitesseX(vitesse * Math.cos(Math.toRadians(angle)));
        setVitesseY(vitesse * Math.sin(Math.toRadians(angle)));
    }

    public void deplacer() {
        x += vitesseX;
        y += vitesseY;
    }

    public void rebondirHorizontal() {
        vitesseY = -vitesseY; // angle d'incidence = angle de réflexion
    }

    public void rebondirVertical() {
        vitesseX = -vitesseX; // angle d'incidence = angle de réflexion
    }

    public int getX() {
        return (int) x; // Retourne la partie entière pour l'affichage
    }

    public int getY() {
        return (int) y; // Retourne la partie entière pour l'affichage
    }

    public int getRayon() {
        return rayon;
    }

    public double getVitesseX() {
        return vitesseX;
    }

    public double getVitesseY() {
        return vitesseY;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    public void setVitesseX(double vitesseX) {
        this.vitesseX = vitesseX;
    }

    public void setVitesseY(double vitesseY) {
        this.vitesseY = vitesseY;
    }

    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    public double getVitesse() {
        return vitesse;
    }

    public boolean collisionAvec(Raquette raquette) {
        // Vérifier si la balle intersecte avec la raquette
        int balleGauche = getX() - rayon;
        int balleDroite = getX() + rayon;
        int balleHaut = getY() - rayon;
        int balleBas = getY() + rayon;
        
        int raquetteGauche = raquette.x;
        int raquetteDroite = raquette.x + raquette.width;
        int raquetteHaut = raquette.y;
        int raquetteBas = raquette.y + raquette.height;
        
        return balleDroite >= raquetteGauche && 
               balleGauche <= raquetteDroite && 
               balleBas >= raquetteHaut && 
               balleHaut <= raquetteBas;
    }
}