package com.example;

public class Raquette extends Form {
    private Joueur joueur;
    private int vitesse = 5;

    public Raquette(int x, int y, int width, int height, Joueur joueur) {
        super(x, y, width, height);
        setJoueur(joueur);
    }

    @Override
    public void deplacer(int dx, int dy) {
        this.x += dx;
        // Limiter le déplacement aux bords
        if (this.x < 0) {
            this.x = 0;
        }
    }

    public void deplacerGauche() {
        deplacer(-vitesse, 0);
    }

    public void deplacerDroite() {
        deplacer(vitesse, 0);
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public int getVitesse() {
        return vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }
}
