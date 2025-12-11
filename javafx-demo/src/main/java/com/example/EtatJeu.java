package com.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EtatJeu implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // État de la balle
    private double balleX;
    private double balleY;
    private double balleVitesseX;
    private double balleVitesseY;
    private double balleVitesse;
    
    // État des raquettes
    private int raquette1X;
    private int raquette2X;
    
    // État des pièces
    private List<EtatPiece> pieces;
    
    // Scores
    private int pointsJoueur1;
    private int pointsJoueur2;
    
    // État du jeu
    private boolean jeuTermine;
    private String nomGagnant;
    
    public EtatJeu() {
        pieces = new ArrayList<>();
    }
    
    // Getters et Setters
    public double getBalleX() { return balleX; }
    public void setBalleX(double balleX) { this.balleX = balleX; }
    
    public double getBalleY() { return balleY; }
    public void setBalleY(double balleY) { this.balleY = balleY; }
    
    public double getBalleVitesseX() { return balleVitesseX; }
    public void setBalleVitesseX(double balleVitesseX) { this.balleVitesseX = balleVitesseX; }
    
    public double getBalleVitesseY() { return balleVitesseY; }
    public void setBalleVitesseY(double balleVitesseY) { this.balleVitesseY = balleVitesseY; }
    
    public double getBalleVitesse() { return balleVitesse; }
    public void setBalleVitesse(double balleVitesse) { this.balleVitesse = balleVitesse; }
    
    public int getRaquette1X() { return raquette1X; }
    public void setRaquette1X(int raquette1X) { this.raquette1X = raquette1X; }
    
    public int getRaquette2X() { return raquette2X; }
    public void setRaquette2X(int raquette2X) { this.raquette2X = raquette2X; }
    
    public List<EtatPiece> getPieces() { return pieces; }
    public void setPieces(List<EtatPiece> pieces) { this.pieces = pieces; }
    
    public int getPointsJoueur1() { return pointsJoueur1; }
    public void setPointsJoueur1(int pointsJoueur1) { this.pointsJoueur1 = pointsJoueur1; }
    
    public int getPointsJoueur2() { return pointsJoueur2; }
    public void setPointsJoueur2(int pointsJoueur2) { this.pointsJoueur2 = pointsJoueur2; }
    
    public boolean isJeuTermine() { return jeuTermine; }
    public void setJeuTermine(boolean jeuTermine) { this.jeuTermine = jeuTermine; }
    
    public String getNomGagnant() { return nomGagnant; }
    public void setNomGagnant(String nomGagnant) { this.nomGagnant = nomGagnant; }
    
    public static class EtatPiece implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private int x;
        private int y;
        private int vie;
        private boolean actif;
        
        public EtatPiece(int x, int y, int vie, boolean actif) {
            this.x = x;
            this.y = y;
            this.vie = vie;
            this.actif = actif;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public int getVie() { return vie; }
        public boolean isActif() { return actif; }
    }
}
