package com.example;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.HashSet;
import java.util.Set;

public class EchiquierView extends Pane {
    private Echiquier echiquier;
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private boolean modeMultijoueur;
    private boolean isServeur;
    private ServeurJeu serveur;
    private ClientJeu client;

    public EchiquierView(Echiquier echiquier, boolean modeMultijoueur, boolean isServeur, 
                        ServeurJeu serveur, ClientJeu client) {
        this.echiquier = echiquier;
        this.modeMultijoueur = modeMultijoueur;
        this.isServeur = isServeur;
        this.serveur = serveur;
        this.client = client;
        
        int taillePlateau = echiquier.getLargeur() * echiquier.getTailleCellule();
        canvas = new Canvas(taillePlateau, taillePlateau);
        gc = canvas.getGraphicsContext2D();
        
        getChildren().add(canvas);
        
        // Permettre au Pane de recevoir le focus
        setFocusTraversable(true);
        
        // Ajouter les contrôles clavier sur le Pane au lieu du canvas
        setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            e.consume(); // Empêcher la propagation de l'événement
        });
        setOnKeyReleased(e -> {
            pressedKeys.remove(e.getCode());
            e.consume();
        });
        
        // Demander le focus au clic sur le canvas
        canvas.setOnMouseClicked(e -> requestFocus());
        
        dessiner();
        demarrerAnimation();
    }

    private void demarrerAnimation() {
        animationTimer = new AnimationTimer() {
            private long dernierEnvoi = 0;
            private static final long INTERVALLE_SYNC = 16_666_667; // ~60 FPS (en nanosecondes)
            
            @Override
            public void handle(long now) {
                if (echiquier.isJeuTermine()) {
                    arreterAnimation();
                    afficherMessageVictoire();
                    return;
                }
                
                if (modeMultijoueur) {
                    if (isServeur) {
                        // Le serveur gère la logique du jeu
                        gererDeplacementsRaquettes();
                        echiquier.deplacerBalle();
                        
                        // Envoyer l'état au client régulièrement
                        if (now - dernierEnvoi >= INTERVALLE_SYNC) {
                            envoyerEtatComplet();
                            dernierEnvoi = now;
                        }
                        
                        // Traiter les messages du client (déplacements uniquement)
                        traiterMessagesClient();
                    } else {
                        // Le client reçoit l'état du serveur
                        recevoirEtatServeur();
                        
                        // Le client envoie seulement ses commandes
                        gererDeplacementsRaquettes();
                    }
                } else {
                    // Mode solo
                    gererDeplacementsRaquettes();
                    echiquier.deplacerBalle();
                }
                
                rafraichir();
            }
        };
        animationTimer.start();
    }

    private void envoyerEtatComplet() {
        if (serveur != null && serveur.isConnecte()) {
            EtatJeu etat = echiquier.extraireEtat();
            serveur.envoyerMessage(new MessageReseau(MessageReseau.TypeMessage.SYNCHRONISATION_COMPLETE, etat));
        }
    }

    private void recevoirEtatServeur() {
        MessageReseau msg;
        while ((msg = client.recupererMessage()) != null) {
            if (msg.getType() == MessageReseau.TypeMessage.SYNCHRONISATION_COMPLETE) {
                EtatJeu etat = (EtatJeu) msg.getData();
                echiquier.appliquerEtat(etat);
            } else if (msg.getType() == MessageReseau.TypeMessage.CONFIGURATION_JEU) {
                ConfigurationJeu config = (ConfigurationJeu) msg.getData();
                javafx.application.Platform.runLater(() -> resetJeuDepuisConfiguration(config));
            }
        }
    }

    private void traiterMessagesClient() {
        MessageReseau msg;
        while ((msg = serveur.recupererMessage()) != null) {
            if (msg.getType() == MessageReseau.TypeMessage.DEPLACEMENT_RAQUETTE) {
                int[] data = (int[]) msg.getData();
                int raquette = data[0];
                int direction = data[1];
                
                // Appliquer le déplacement de la raquette du client (raquette 2)
                if (raquette == 2) {
                    if (direction == -1) echiquier.deplacerRaquette2Gauche();
                    else echiquier.deplacerRaquette2Droite();
                }
            } else if (msg.getType() == MessageReseau.TypeMessage.CHANGEMENT_VITESSE) {
                double nouvelleVitesse = (Double) msg.getData();
                echiquier.setVitesseBalle(nouvelleVitesse);
            }
        }
    }

    private void traiterMessagesReseau() {
        MessageReseau msg = isServeur ? serveur.recupererMessage() : client.recupererMessage();
        
        while (msg != null) {
            if (msg.getType() == MessageReseau.TypeMessage.DEPLACEMENT_RAQUETTE) {
                int[] data = (int[]) msg.getData();
                int raquette = data[0]; // 1 ou 2
                int direction = data[1]; // -1 gauche, 1 droite
                
                if (raquette == 1) {
                    if (direction == -1) echiquier.deplacerRaquette1Gauche();
                    else echiquier.deplacerRaquette1Droite();
                } else {
                    if (direction == -1) echiquier.deplacerRaquette2Gauche();
                    else echiquier.deplacerRaquette2Droite();
                }
            } else if (msg.getType() == MessageReseau.TypeMessage.CHANGEMENT_VITESSE) {
                double nouvelleVitesse = (Double) msg.getData();
                echiquier.setVitesseBalle(nouvelleVitesse);
            } else if (msg.getType() == MessageReseau.TypeMessage.CONFIGURATION_JEU && !isServeur) {
                // Le client reçoit une nouvelle configuration = reset du jeu
                ConfigurationJeu config = (ConfigurationJeu) msg.getData();
                javafx.application.Platform.runLater(() -> {
                    // Notifier l'App pour recréer le jeu
                    resetJeuDepuisConfiguration(config);
                });
            }
            
            msg = isServeur ? serveur.recupererMessage() : client.recupererMessage();
        }
    }

    private void resetJeuDepuisConfiguration(ConfigurationJeu config) {
        // Arrêter l'animation actuelle
        arreterAnimation();
        
        // Créer un événement personnalisé pour notifier l'App
        fireEvent(new javafx.event.Event(javafx.event.EventType.ROOT));
        
        // Ou directement recréer l'échiquier si on a accès à l'App
        // Pour l'instant, on peut juste arrêter et afficher un message
        System.out.println("Nouvelle partie reçue du serveur !");
    }

    private void gererDeplacementsRaquettes() {
        if (modeMultijoueur) {
            if (isServeur) {
                // Serveur = Joueur 1 (bas) - contrôle direct
                if (pressedKeys.contains(KeyCode.LEFT)) {
                    echiquier.deplacerRaquette1Gauche();
                }
                if (pressedKeys.contains(KeyCode.RIGHT)) {
                    echiquier.deplacerRaquette1Droite();
                }
            } else {
                // Client = Joueur 2 (haut) - envoie les commandes
                if (pressedKeys.contains(KeyCode.A)) {
                    echiquier.deplacerRaquette2Gauche();
                    envoyerDeplacementRaquette(2, -1);
                }
                if (pressedKeys.contains(KeyCode.D)) {
                    echiquier.deplacerRaquette2Droite();
                    envoyerDeplacementRaquette(2, 1);
                }
            }
        } else {
            // Mode solo - contrôles normaux
            if (pressedKeys.contains(KeyCode.LEFT)) {
                echiquier.deplacerRaquette1Gauche();
            }
            if (pressedKeys.contains(KeyCode.RIGHT)) {
                echiquier.deplacerRaquette1Droite();
            }
            if (pressedKeys.contains(KeyCode.A)) {
                echiquier.deplacerRaquette2Gauche();
            }
            if (pressedKeys.contains(KeyCode.D)) {
                echiquier.deplacerRaquette2Droite();
            }
        }
    }

    private void envoyerDeplacementRaquette(int raquette, int direction) {
        int[] data = {raquette, direction};
        MessageReseau msg = new MessageReseau(MessageReseau.TypeMessage.DEPLACEMENT_RAQUETTE, data);
        if (isServeur && serveur != null) {
            serveur.envoyerMessage(msg);
        } else if (client != null) {
            client.envoyerMessage(msg);
        }
    }

    private void dessiner() {
        dessinerPlateau();
        dessinerRaquettes();
        dessinerPieces();
        dessinerBalle();
        
        if (echiquier.isJeuTermine()) {
            afficherMessageVictoire();
        }
    }

    private void dessinerPlateau() {
        int tailleCellule = echiquier.getTailleCellule();
        int largeur = echiquier.getLargeur();

        for (int i = 0; i < largeur; i++) {
            for (int j = 0; j < largeur; j++) {
                // Damier: alternance de couleurs
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.WHEAT);
                } else {
                    gc.setFill(Color.SADDLEBROWN);
                }
                gc.fillRect(i * tailleCellule, j * tailleCellule, tailleCellule, tailleCellule);
                
                // Bordure des cases
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(0.5);
                gc.strokeRect(i * tailleCellule, j * tailleCellule, tailleCellule, tailleCellule);
            }
        }
    }

    private void dessinerRaquettes() {
        // Raquette Joueur 1 (bleu)
        Raquette r1 = echiquier.getRaquetteJoueur1();
        gc.setFill(Color.BLUE);
        gc.fillRect(r1.x, r1.y, r1.width, r1.height);
        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(2);
        gc.strokeRect(r1.x, r1.y, r1.width, r1.height);
        
        // Raquette Joueur 2 (rouge)
        Raquette r2 = echiquier.getRaquetteJoueur2();
        gc.setFill(Color.RED);
        gc.fillRect(r2.x, r2.y, r2.width, r2.height);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokeRect(r2.x, r2.y, r2.width, r2.height);
        
        // Afficher les contrôles
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 10));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("← / →", r1.x + r1.width / 2, r1.y + r1.height / 2 + 3);
        gc.fillText("A / D", r2.x + r2.width / 2, r2.y + r2.height / 2 + 3);
    }

    private void dessinerPieces() {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", 18));

        for (Piece piece : echiquier.getPieces()) {
            if (piece.isStatus()) {
                dessinerPiece(piece);
            }
        }
    }

    private void dessinerPiece(Piece piece) {
        int tailleCellule = echiquier.getTailleCellule();
        
        // Couleur selon le joueur
        if (piece.getJoueur().equals(echiquier.getJoueur1())) {
            gc.setFill(Color.LIGHTBLUE);
            gc.setStroke(Color.DARKBLUE);
        } else {
            gc.setFill(Color.LIGHTCORAL);
            gc.setStroke(Color.DARKRED);
        }

        // Dessiner le cercle représentant la pièce
        int x = piece.x;
        int y = piece.y;
        int marge = 5;
        
        gc.fillOval(x + marge, y + marge, tailleCellule - 2 * marge, tailleCellule - 2 * marge);
        gc.setLineWidth(2);
        gc.strokeOval(x + marge, y + marge, tailleCellule - 2 * marge, tailleCellule - 2 * marge);

        // Afficher le symbole de la pièce
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 10));
        String symbole = getSymbolePiece(piece.getType());
        gc.fillText(symbole, x + tailleCellule / 2, y + tailleCellule / 2 - 5);

        // Afficher les stats (vie/puissance)
        gc.setFont(Font.font("Arial", 8));
        String stats = piece.getVie() + "/" + piece.getPuissance();
        gc.fillText(stats, x + tailleCellule / 2, y + tailleCellule / 2 + 8);
    }

    private String getSymbolePiece(TypePiece type) {
        switch (type) {
            case ROI: return "♔";
            case REINE: return "♕";
            case TOUR: return "♖";
            case FOU: return "♗";
            case CAVALIER: return "♘";
            case PION: return "♙";
            default: return "?";
        }
    }

    private void dessinerBalle() {
        Balle balle = echiquier.getBalle();
        
        // Dessiner la balle
        gc.setFill(Color.RED);
        gc.fillOval(
            balle.getX() - balle.getRayon(), 
            balle.getY() - balle.getRayon(), 
            balle.getRayon() * 2, 
            balle.getRayon() * 2
        );
        
        // Bordure de la balle
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(1);
        gc.strokeOval(
            balle.getX() - balle.getRayon(), 
            balle.getY() - balle.getRayon(), 
            balle.getRayon() * 2, 
            balle.getRayon() * 2
        );
    }

    public void rafraichir() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        dessiner();
    }

    public void arreterAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    private void afficherMessageVictoire() {
        int taillePlateau = echiquier.getLargeur() * echiquier.getTailleCellule();
        
        // Fond semi-transparent
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, taillePlateau, taillePlateau);
        
        // Message de victoire
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", 36));
        gc.setTextAlign(TextAlignment.CENTER);
        
        String message = "🎉 " + echiquier.getGagnant().getNom() + " a gagné ! 🎉";
        gc.fillText(message, taillePlateau / 2, taillePlateau / 2 - 20);
        
        gc.setFont(Font.font("Arial", 18));
        gc.setFill(Color.WHITE);
        gc.fillText("Le roi adverse a été détruit !", taillePlateau / 2, taillePlateau / 2 + 20);
    }
}
