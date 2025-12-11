package com.example;

import java.io.Serializable;

public class ConfigurationJeu implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int nbPions;
    private String nomJoueur1;
    private String nomJoueur2;
    private int joueurPremier;
    private double angle;
    private int vieRoi;
    private int vieReine;
    private int vieTour;
    private int vieFou;
    private int vieCavalier;
    private int viePion;
    
    public ConfigurationJeu(int nbPions, String nomJoueur1, String nomJoueur2, int joueurPremier, 
                           double angle, int vieRoi, int vieReine, int vieTour, int vieFou, 
                           int vieCavalier, int viePion) {
        this.nbPions = nbPions;
        this.nomJoueur1 = nomJoueur1;
        this.nomJoueur2 = nomJoueur2;
        this.joueurPremier = joueurPremier;
        this.angle = angle;
        this.vieRoi = vieRoi;
        this.vieReine = vieReine;
        this.vieTour = vieTour;
        this.vieFou = vieFou;
        this.vieCavalier = vieCavalier;
        this.viePion = viePion;
    }
    
    // Getters
    public int getNbPions() { return nbPions; }
    public String getNomJoueur1() { return nomJoueur1; }
    public String getNomJoueur2() { return nomJoueur2; }
    public int getJoueurPremier() { return joueurPremier; }
    public double getAngle() { return angle; }
    public int getVieRoi() { return vieRoi; }
    public int getVieReine() { return vieReine; }
    public int getVieTour() { return vieTour; }
    public int getVieFou() { return vieFou; }
    public int getVieCavalier() { return vieCavalier; }
    public int getViePion() { return viePion; }
}
