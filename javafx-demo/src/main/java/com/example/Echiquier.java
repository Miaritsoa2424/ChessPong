package com.example;

import java.util.ArrayList;
import java.util.List;

public class Echiquier {
    private Joueur joueur1;
    private Joueur joueur2;
    private int nombrePion;
    private Joueur joueurPremier;
    private int largeur = 8; // Toujours 8x8
    private int tailleCellule = 50; // Taille d'une cellule en pixels
    private List<Piece> pieces;
    private Balle balle;
    private boolean jeuTermine = false;
    private Joueur gagnant = null;
    private Piece dernierePiecePercutee = null;
    private int framesSansPiece = 0;
    private int numeroJoueurPremier; // 1 ou 2
    private double angleInitial;
    private int vieRoi;
    private int vieReine;
    private int vieTour;
    private int vieFou;
    private int vieCavalier;
    private int viePion;
    private Raquette raquetteJoueur1;
    private Raquette raquetteJoueur2;

    public Echiquier(Joueur joueur1, Joueur joueur2, int nbPion, int joueurPremier, double angle,
                    int vieRoi, int vieReine, int vieTour, int vieFou, int vieCavalier, int viePion) {
        setJoueur1(joueur1);
        setJoueur2(joueur2);
        setNombrePion(nbPion);
        this.numeroJoueurPremier = joueurPremier;
        this.angleInitial = angle;
        
        // Stocker les vies configurées
        this.vieRoi = vieRoi;
        this.vieReine = vieReine;
        this.vieTour = vieTour;
        this.vieFou = vieFou;
        this.vieCavalier = vieCavalier;
        this.viePion = viePion;
        
        if (joueurPremier == 1) {
            setJoueurPremier(joueur1);
        } else {
            setJoueurPremier(joueur2);
        }
        
        this.pieces = new ArrayList<>();
        initialiserPlateau();
        initialiserRaquettes();
        initialiserBalle();
    }

    public Joueur getJoueur1() {
        return joueur1;
    }

    public void setJoueur1(Joueur joueur1) {
        this.joueur1 = joueur1;
    }

    public Joueur getJoueur2() {
        return joueur2;
    }

    public void setJoueur2(Joueur joueur2) {
        this.joueur2 = joueur2;
    }

    public int getNombrePion() {
        return nombrePion;
    }

    public void setNombrePion(int nombrePion) {
        if (nombrePion % 2 == 0) {
            this.nombrePion = nombrePion;
        }
        else {
            this.nombrePion = nombrePion + 1;
        }
    }

    public Joueur getJoueurPremier() {
        return joueurPremier;
    }

    public void setJoueurPremier(Joueur joueurPremier) {
        this.joueurPremier = joueurPremier;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        // La largeur reste toujours 8
        this.largeur = 8;
    }

    public int getTailleCellule() {
        return tailleCellule;
    }

    public void setTailleCellule(int tailleCellule) {
        this.tailleCellule = tailleCellule;
    }

    /**
     * Initialise le plateau avec les pièces selon nbPion
     * L'échiquier est toujours 8x8, seules les pièces sont placées selon nbPion
     * nbPion = 2: Roi + Reine pour chaque joueur + Pions devant les pièces majeures
     * nbPion = 4: Roi + Reine + 2 Tours pour chaque joueur + Pions devant les pièces majeures
     * nbPion = 6: Roi + Reine + 2 Tours + 2 Fous pour chaque joueur + Pions devant les pièces majeures
     * nbPion = 8: Roi + Reine + 2 Tours + 2 Fous + 2 Cavaliers pour chaque joueur + Pions devant les pièces majeures
     * nbPion >= 10: Roi + Reine + 2 Tours + 2 Fous + 2 Cavaliers + Pions pour chaque joueur
     */
    public void initialiserPlateau() {
        pieces.clear();
        
        // Calcul de la position centrale sur l'échiquier 8x8
        int debutPosition = (8 - nombrePion) / 2;
        int positionCentreGauche = debutPosition + (nombrePion / 2) - 1;
        int positionCentreDroite = debutPosition + (nombrePion / 2);

        // Joueur 1 - ligne du bas (y = 7 et 6)
        int ligneJ1Majeur = 7;
        int ligneJ1Pion = 6;

        // Joueur 2 - ligne du haut (y = 0 et 1)
        int ligneJ2Majeur = 0;
        int ligneJ2Pion = 1;

        // Placement du Roi et de la Reine (toujours présents si nbPion >= 2)
        if (nombrePion >= 2) {
            // Joueur 1
            pieces.add(new Piece(positionCentreGauche * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieRoi, 10, TypePiece.ROI));
            pieces.add(new Piece(positionCentreDroite * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieReine, 9, TypePiece.REINE));
            
            // Pions devant Roi et Reine
            pieces.add(new Piece(positionCentreGauche * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));
            pieces.add(new Piece(positionCentreDroite * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));

            // Joueur 2
            pieces.add(new Piece(positionCentreGauche * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieRoi, 10, TypePiece.ROI));
            pieces.add(new Piece(positionCentreDroite * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieReine, 9, TypePiece.REINE));
            
            // Pions devant Roi et Reine
            pieces.add(new Piece(positionCentreGauche * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
            pieces.add(new Piece(positionCentreDroite * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
        }

        // Ajout des Tours (si nbPion >= 4)
        if (nombrePion >= 4) {
            // Joueur 1
            pieces.add(new Piece((positionCentreGauche - 1) * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieTour, 5, TypePiece.TOUR));
            pieces.add(new Piece((positionCentreDroite + 1) * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieTour, 5, TypePiece.TOUR));
            
            // Pions devant les Tours
            pieces.add(new Piece((positionCentreGauche - 1) * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));
            pieces.add(new Piece((positionCentreDroite + 1) * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));

            // Joueur 2
            pieces.add(new Piece((positionCentreGauche - 1) * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieTour, 5, TypePiece.TOUR));
            pieces.add(new Piece((positionCentreDroite + 1) * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieTour, 5, TypePiece.TOUR));
            
            // Pions devant les Tours
            pieces.add(new Piece((positionCentreGauche - 1) * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
            pieces.add(new Piece((positionCentreDroite + 1) * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
        }

        // Ajout des Fous (si nbPion >= 6)
        if (nombrePion >= 6) {
            // Joueur 1
            pieces.add(new Piece((positionCentreGauche - 2) * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieFou, 3, TypePiece.FOU));
            pieces.add(new Piece((positionCentreDroite + 2) * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieFou, 3, TypePiece.FOU));
            
            // Pions devant les Fous
            pieces.add(new Piece((positionCentreGauche - 2) * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));
            pieces.add(new Piece((positionCentreDroite + 2) * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));

            // Joueur 2
            pieces.add(new Piece((positionCentreGauche - 2) * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieFou, 3, TypePiece.FOU));
            pieces.add(new Piece((positionCentreDroite + 2) * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieFou, 3, TypePiece.FOU));
            
            // Pions devant les Fous
            pieces.add(new Piece((positionCentreGauche - 2) * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
            pieces.add(new Piece((positionCentreDroite + 2) * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
        }

        // Ajout des Cavaliers (si nbPion >= 8)
        if (nombrePion >= 8) {
            // Joueur 1
            pieces.add(new Piece((positionCentreGauche - 3) * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieCavalier, 3, TypePiece.CAVALIER));
            pieces.add(new Piece((positionCentreDroite + 3) * tailleCellule, ligneJ1Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, vieCavalier, 3, TypePiece.CAVALIER));
            
            // Pions devant les Cavaliers
            pieces.add(new Piece((positionCentreGauche - 3) * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));
            pieces.add(new Piece((positionCentreDroite + 3) * tailleCellule, ligneJ1Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur1, viePion, 1, TypePiece.PION));

            // Joueur 2
            pieces.add(new Piece((positionCentreGauche - 3) * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieCavalier, 3, TypePiece.CAVALIER));
            pieces.add(new Piece((positionCentreDroite + 3) * tailleCellule, ligneJ2Majeur * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, vieCavalier, 3, TypePiece.CAVALIER));
            
            // Pions devant les Cavaliers
            pieces.add(new Piece((positionCentreGauche - 3) * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
            pieces.add(new Piece((positionCentreDroite + 3) * tailleCellule, ligneJ2Pion * tailleCellule, 
                               tailleCellule, tailleCellule, joueur2, viePion, 1, TypePiece.PION));
        }
    }

    private void initialiserRaquettes() {
        int largeurRaquette = 100;
        int hauteurRaquette = 10;
        int taillePlateau = largeur * tailleCellule;
        
        // Raquette Joueur 1 (devant ses pions - ligne 5)
        int xRaquette1 = (taillePlateau - largeurRaquette) / 2;
        int yRaquette1 = 6 * tailleCellule;  // Ligne 5 (devant les pions du joueur 1)
        raquetteJoueur1 = new Raquette(xRaquette1, yRaquette1, largeurRaquette, hauteurRaquette, joueur1);
        
        // Raquette Joueur 2 (devant ses pions - ligne 2)
        int xRaquette2 = (taillePlateau - largeurRaquette) / 2;
        int yRaquette2 = 2 * tailleCellule;  // Ligne 2 (devant les pions du joueur 2)
        raquetteJoueur2 = new Raquette(xRaquette2, yRaquette2, largeurRaquette, hauteurRaquette, joueur2);
    }

    private void initialiserBalle() {
        int centreX = (largeur * tailleCellule) / 2;
        int centreY;
        
        // Positionner la balle devant les pions du joueur qui commence
        if (numeroJoueurPremier == 1) {
            // Joueur 1 commence (en bas) - balle devant ses pions (ligne 5)
            centreY = 5 * tailleCellule + tailleCellule / 2;
        } else {
            // Joueur 2 commence (en haut) - balle devant ses pions (ligne 2)
            centreY = 2 * tailleCellule + tailleCellule / 2;
        }
        
        int rayon = 10;
        double vitesse = 1.5;
        
        // Ajuster l'angle selon le joueur qui commence
        // Si Joueur 2 commence (en haut), inverser la direction verticale
        double angle = angleInitial;
        if (numeroJoueurPremier == 2) {
            // Inverser l'angle pour que la balle aille vers le bas
            angle = 360 - angleInitial;
        }
        
        balle = new Balle(centreX, centreY, rayon, vitesse, angle);
    }

    public void deplacerBalle() {
        balle.deplacer();
        
        // Vérifier les collisions avec les bords
        int taillePlateau = largeur * tailleCellule;
        
        // Rebond sur les bords horizontaux (seulement si pas de raquette)
        if (balle.getY() - balle.getRayon() <= 0 || balle.getY() + balle.getRayon() >= taillePlateau) {
            balle.rebondirHorizontal();
        }
        
        // Rebond sur les bords verticaux
        if (balle.getX() - balle.getRayon() <= 0 || balle.getX() + balle.getRayon() >= taillePlateau) {
            balle.rebondirVertical();
        }
        
        // Vérifier les collisions avec les raquettes
        verifierCollisionsRaquettes();
        
        // Vérifier les collisions avec les pièces
        verifierCollisionsPieces();
    }

    private void verifierCollisionsRaquettes() {
        // Collision avec raquette Joueur 1 (en bas)
        if (balle.collisionAvec(raquetteJoueur1)) {
            balle.rebondirHorizontal();
            // Repositionner la balle au-dessus de la raquette
            balle.setY(raquetteJoueur1.y - balle.getRayon());
        }
        
        // Collision avec raquette Joueur 2 (en haut)
        if (balle.collisionAvec(raquetteJoueur2)) {
            balle.rebondirHorizontal();
            // Repositionner la balle en-dessous de la raquette
            balle.setY(raquetteJoueur2.y + raquetteJoueur2.height + balle.getRayon());
        }
    }

    private void verifierCollisionsPieces() {
        boolean collisionDetectee = false;
        
        for (Piece piece : pieces) {
            if (!piece.isStatus()) continue;
            
            // Calculer le centre de la pièce
            int pieceCentreX = piece.x + tailleCellule / 2;
            int pieceCentreY = piece.y + tailleCellule / 2;
            
            // Distance entre le centre de la balle et le centre de la pièce
            double distance = Math.sqrt(
                Math.pow(balle.getX() - pieceCentreX, 2) + 
                Math.pow(balle.getY() - pieceCentreY, 2)
            );
            
            // Rayon de collision approximatif (rayon de la balle + demi-taille de la pièce)
            double rayonCollision = balle.getRayon() + (tailleCellule / 2);
            
            if (distance < rayonCollision) {
                // Éviter de percuter la même pièce plusieurs fois de suite
                if (dernierePiecePercutee != piece) {
                    gererCollisionPiece(piece, pieceCentreX, pieceCentreY);
                    dernierePiecePercutee = piece;
                    framesSansPiece = 0;
                }
                collisionDetectee = true;
                break;
            }
        }
        
        // Réinitialiser après quelques frames sans collision
        if (!collisionDetectee) {
            framesSansPiece++;
            if (framesSansPiece > 5) {
                dernierePiecePercutee = null;
            }
        }
    }

    private void gererCollisionPiece(Piece piece, int pieceCentreX, int pieceCentreY) {
        // Calculer l'angle de collision
        double dx = balle.getX() - pieceCentreX;
        double dy = balle.getY() - pieceCentreY;
        
        // Déterminer la direction du rebond
        if (Math.abs(dx) > Math.abs(dy)) {
            balle.rebondirVertical();
        } else {
            balle.rebondirHorizontal();
        }
        
        // Éloigner légèrement la balle de la pièce pour éviter les collisions multiples
        double angle = Math.atan2(dy, dx);
        double distance = 2.0; // Distance de séparation
        balle.setX((int)(pieceCentreX + Math.cos(angle) * (tailleCellule / 2.0 + balle.getRayon() + distance)));
        balle.setY((int)(pieceCentreY + Math.sin(angle) * (tailleCellule / 2.0 + balle.getRayon() + distance)));
        
        // Réduire la vie de la pièce
        int nouvelleVie = piece.getVie() - 1;
        piece.setVie(nouvelleVie);
        
        // Désactiver la pièce si sa vie est épuisée
        if (nouvelleVie <= 0) {
            piece.setStatus(false);
            
            // Vérifier si c'est un roi
            if (piece.getType() == TypePiece.ROI) {
                jeuTermine = true;
                // Le gagnant est le joueur adverse
                if (piece.getJoueur().equals(joueur1)) {
                    gagnant = joueur2;
                } else {
                    gagnant = joueur1;
                }
                System.out.println("Fin du jeu ! Le roi de " + piece.getJoueur().getNom() + " a été détruit !");
                System.out.println("Gagnant : " + gagnant.getNom());
            } else {
                // Ajouter des points au joueur adverse
                if (piece.getJoueur().equals(joueur1)) {
                    joueur2.ajouterPoint(piece.getPuissance());
                } else {
                    joueur1.ajouterPoint(piece.getPuissance());
                }
            }
        }
    }

    public boolean isJeuTermine() {
        return jeuTermine;
    }

    public Joueur getGagnant() {
        return gagnant;
    }

    public Balle getBalle() {
        return balle;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Raquette getRaquetteJoueur1() {
        return raquetteJoueur1;
    }

    public Raquette getRaquetteJoueur2() {
        return raquetteJoueur2;
    }

    public void deplacerRaquette1Gauche() {
        raquetteJoueur1.deplacerGauche();
        // Limiter aux bords
        if (raquetteJoueur1.x < 0) {
            raquetteJoueur1.x = 0;
        }
    }

    public void deplacerRaquette1Droite() {
        raquetteJoueur1.deplacerDroite();
        int taillePlateau = largeur * tailleCellule;
        if (raquetteJoueur1.x + raquetteJoueur1.width > taillePlateau) {
            raquetteJoueur1.x = taillePlateau - raquetteJoueur1.width;
        }
    }

    public void deplacerRaquette2Gauche() {
        raquetteJoueur2.deplacerGauche();
        if (raquetteJoueur2.x < 0) {
            raquetteJoueur2.x = 0;
        }
    }

    public void deplacerRaquette2Droite() {
        raquetteJoueur2.deplacerDroite();
        int taillePlateau = largeur * tailleCellule;
        if (raquetteJoueur2.x + raquetteJoueur2.width > taillePlateau) {
            raquetteJoueur2.x = taillePlateau - raquetteJoueur2.width;
        }
    }

    public void augmenterVitesseBalle(double increment) {
        double nouvelleVitesse = balle.getVitesse() + increment;
        if (nouvelleVitesse > 0) {
            setVitesseBalle(nouvelleVitesse);
        }
    }

    public void diminuerVitesseBalle(double decrement) {
        double nouvelleVitesse = balle.getVitesse() - decrement;
        if (nouvelleVitesse > 0.1) { // Vitesse minimale de 0.1
            setVitesseBalle(nouvelleVitesse);
        }
    }
    
    public void setVitesseBalle(double nouvelleVitesse) {
        balle.setVitesse(nouvelleVitesse);
        // Recalculer les composantes de vitesse en gardant la direction
        double angle = Math.atan2(balle.getVitesseY(), balle.getVitesseX());
        balle.setVitesseX(nouvelleVitesse * Math.cos(angle));
        balle.setVitesseY(nouvelleVitesse * Math.sin(angle));
    }

    public EtatJeu extraireEtat() {
        EtatJeu etat = new EtatJeu();
        
        // État de la balle
        etat.setBalleX(balle.getX());
        etat.setBalleY(balle.getY());
        etat.setBalleVitesseX(balle.getVitesseX());
        etat.setBalleVitesseY(balle.getVitesseY());
        etat.setBalleVitesse(balle.getVitesse());
        
        // État des raquettes
        etat.setRaquette1X(raquetteJoueur1.x);
        etat.setRaquette2X(raquetteJoueur2.x);
        
        // État des pièces
        List<EtatJeu.EtatPiece> etatPieces = new ArrayList<>();
        for (Piece piece : pieces) {
            etatPieces.add(new EtatJeu.EtatPiece(piece.x, piece.y, piece.getVie(), piece.isStatus()));
        }
        etat.setPieces(etatPieces);
        
        // Scores
        etat.setPointsJoueur1(joueur1.getPoint());
        etat.setPointsJoueur2(joueur2.getPoint());
        
        // État du jeu
        etat.setJeuTermine(jeuTermine);
        if (gagnant != null) {
            etat.setNomGagnant(gagnant.getNom());
        }
        
        return etat;
    }
    
    public void appliquerEtat(EtatJeu etat) {
        // Appliquer l'état de la balle
        balle.setX((int) etat.getBalleX());
        balle.setY((int) etat.getBalleY());
        balle.setVitesseX(etat.getBalleVitesseX());
        balle.setVitesseY(etat.getBalleVitesseY());
        balle.setVitesse(etat.getBalleVitesse());
        
        // Appliquer l'état des raquettes
        raquetteJoueur1.x = etat.getRaquette1X();
        raquetteJoueur2.x = etat.getRaquette2X();
        
        // Appliquer l'état des pièces
        List<EtatJeu.EtatPiece> etatPieces = etat.getPieces();
        for (int i = 0; i < pieces.size() && i < etatPieces.size(); i++) {
            EtatJeu.EtatPiece ep = etatPieces.get(i);
            Piece piece = pieces.get(i);
            piece.x = ep.getX();
            piece.y = ep.getY();
            piece.setVie(ep.getVie());
            piece.setStatus(ep.isActif());
        }
        
        // Appliquer les scores
        joueur1.setPoint(etat.getPointsJoueur1());
        joueur2.setPoint(etat.getPointsJoueur2());
        
        // Appliquer l'état du jeu
        jeuTermine = etat.isJeuTermine();
        if (etat.getNomGagnant() != null) {
            if (etat.getNomGagnant().equals(joueur1.getNom())) {
                gagnant = joueur1;
            } else {
                gagnant = joueur2;
            }
        }
    }
}