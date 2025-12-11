package com.example;


public class Piece extends Form {
    private boolean status; // actif ou inactif
    private int vie;
    private Joueur joueur;
    private int puissance;
    private TypePiece type;

    public Piece(int x, int y, int width, int height, Joueur joueur, int vie, int puissance, TypePiece type) {
        super(x, y, width, height);
        setJoueur(joueur);
        setVie(vie);
        setPuissance(puissance);
        setStatus(true);
        setType(type);
    }

    @Override
    public void deplacer(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getVie() {
        return vie;
    }

    public void setVie(int vie) {
        this.vie = vie;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public int getPuissance() {
        return puissance;
    }

    public void setPuissance(int puissance) {
        this.puissance = puissance;
    }

    public TypePiece getType() {
        return type;
    }

    public void setType(TypePiece type) {
        this.type = type;
    }

    
}