package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    private EchiquierView echiquierView;
    private BorderPane root;
    private Label scoreLabel;
    private Label vitesseLabel;
    private Joueur joueur1;
    private Joueur joueur2;
    private Echiquier echiquier;

    // Champs pour configurer la vie des pièces
    private TextField txtVieRoi;
    private TextField txtVieReine;
    private TextField txtVieTour;
    private TextField txtVieFou;
    private TextField txtVieCavalier;
    private TextField txtViePion;

    private ServeurJeu serveur;
    private ClientJeu client;
    private boolean isServeur = false;
    private boolean modeMultijoueur = false;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        
        // Afficher d'abord l'écran de choix de mode
        VBox modePanel = createModeSelectionPanel();
        root.setCenter(modePanel);

        Scene scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.setTitle("Échiquier - Sélection du mode");
        stage.show();
        
        stage.setOnCloseRequest(e -> {
            if (serveur != null) serveur.arreter();
            if (client != null) client.arreter();
        });
    }

    private VBox createModeSelectionPanel() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(50));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Choisissez le mode de jeu");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btnSolo = new Button("Jeu Solo");
        btnSolo.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 50px;");
        btnSolo.setOnAction(e -> {
            modeMultijoueur = false;
            afficherConfigurationJeu();
        });

        Button btnServeur = new Button("Créer une partie (Serveur)");
        btnServeur.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 50px;");
        btnServeur.setOnAction(e -> afficherConfigurationServeur());

        Button btnClient = new Button("Rejoindre une partie (Client)");
        btnClient.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 50px;");
        btnClient.setOnAction(e -> afficherConfigurationClient());

        vbox.getChildren().addAll(titleLabel, btnSolo, btnServeur, btnClient);
        return vbox;
    }

    private void afficherConfigurationServeur() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Configuration du Serveur");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label labelPort = new Label("Port:");
        TextField txtPort = new TextField("5555");
        txtPort.setMaxWidth(200);

        Button btnDemarrer = new Button("Démarrer le serveur");
        btnDemarrer.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 14px;");

        btnDemarrer.setOnAction(e -> {
            try {
                int port = Integer.parseInt(txtPort.getText());
                serveur = new ServeurJeu(port);
                serveur.start();
                isServeur = true;
                modeMultijoueur = true;
                
                statusLabel.setText("Serveur démarré. En attente de connexion...");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                
                // Attendre la connexion dans un thread séparé
                new Thread(() -> {
                    while (!serveur.isConnecte()) {
                        try { Thread.sleep(100); } catch (InterruptedException ex) {}
                    }
                    javafx.application.Platform.runLater(() -> {
                        statusLabel.setText("Client connecté ! Configuration du jeu...");
                        afficherConfigurationJeu();
                    });
                }).start();
                
            } catch (Exception ex) {
                statusLabel.setText("Erreur: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        vbox.getChildren().addAll(titleLabel, labelPort, txtPort, btnDemarrer, statusLabel);
        root.setCenter(vbox);
    }

    private void afficherConfigurationClient() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Rejoindre une partie");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label labelIP = new Label("Adresse IP:");
        TextField txtIP = new TextField("localhost");
        txtIP.setMaxWidth(200);

        Label labelPort = new Label("Port:");
        TextField txtPort = new TextField("5555");
        txtPort.setMaxWidth(200);

        Button btnConnecter = new Button("Se connecter");
        btnConnecter.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 14px;");

        btnConnecter.setOnAction(e -> {
            try {
                String host = txtIP.getText();
                int port = Integer.parseInt(txtPort.getText());
                client = new ClientJeu(host, port);
                client.start();
                isServeur = false;
                modeMultijoueur = true;
                
                statusLabel.setText("Connecté au serveur ! En attente de configuration...");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                
                // Thread pour écouter les reconfigurations
                demarrerEcouteReconfigurations();
                
            } catch (Exception ex) {
                statusLabel.setText("Erreur: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(titleLabel, labelIP, txtIP, labelPort, txtPort, btnConnecter, statusLabel);
        root.setCenter(vbox);
    }

    private void demarrerEcouteReconfigurations() {
        new Thread(() -> {
            while (client != null && client.isConnecte()) {
                MessageReseau msg = client.recupererMessage();
                if (msg != null && msg.getType() == MessageReseau.TypeMessage.CONFIGURATION_JEU) {
                    ConfigurationJeu config = (ConfigurationJeu) msg.getData();
                    javafx.application.Platform.runLater(() -> 
                        demarrerJeuMultijoueur(config));
                }
                try { Thread.sleep(100); } catch (InterruptedException ex) {}
            }
        }).start();
    }

    private void afficherConfigurationJeu() {
        root.setTop(createConfigPanel());
        root.setRight(createVieConfigPanel());
        root.setCenter(null);
        root.setBottom(null);
    }

    private VBox createConfigPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Configuration de l'échiquier");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox configBox = new HBox(10);
        configBox.setAlignment(Pos.CENTER);

        Label labelNbPions = new Label("Nombre de pions par côté:");
        TextField textFieldNbPions = new TextField();
        textFieldNbPions.setPromptText("Ex: 2, 4, 6, 8");
        textFieldNbPions.setPrefWidth(100);

        Label labelNomJ1 = new Label("Nom du Joueur 1:");
        TextField textFieldNomJ1 = new TextField();
        textFieldNomJ1.setPromptText("Joueur 1");
        textFieldNomJ1.setPrefWidth(120);

        Label labelNomJ2 = new Label("Nom du Joueur 2:");
        TextField textFieldNomJ2 = new TextField();
        textFieldNomJ2.setPromptText("Joueur 2");
        textFieldNomJ2.setPrefWidth(120);

        // Nouveau: Choix du joueur qui commence
        Label labelPremier = new Label("Qui commence:");
        javafx.scene.control.ComboBox<String> comboPremier = new javafx.scene.control.ComboBox<>();
        comboPremier.getItems().addAll("Joueur 1", "Joueur 2");
        comboPremier.setValue("Joueur 1");
        comboPremier.setPrefWidth(120);

        // Nouveau: Choix de l'angle initial
        Label labelAngle = new Label("Angle initial (0-360°):");
        TextField textFieldAngle = new TextField();
        textFieldAngle.setPromptText("Ex: 45");
        textFieldAngle.setText("45");
        textFieldAngle.setPrefWidth(100);

        Button btnCreer = new Button("Créer l'échiquier");
        btnCreer.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        Label infoLabel = new Label("Échiquier 8x8 - Nombre de pions par côté (2-8, pair uniquement)");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        btnCreer.setOnAction(e -> {
            try {
                int nbPions = Integer.parseInt(textFieldNbPions.getText());
                if (nbPions < 2) {
                    showError("Le nombre de pions doit être au minimum 2");
                    return;
                }
                if (nbPions > 8) {
                    showError("Le nombre de pions ne peut pas dépasser 8");
                    return;
                }
                
                String nomJ1 = textFieldNomJ1.getText().isEmpty() ? "Joueur 1" : textFieldNomJ1.getText();
                String nomJ2 = textFieldNomJ2.getText().isEmpty() ? "Joueur 2" : textFieldNomJ2.getText();
                
                int joueurPremier = comboPremier.getValue().equals("Joueur 1") ? 1 : 2;
                
                double angle = Double.parseDouble(textFieldAngle.getText());
                if (angle < 0 || angle > 360) {
                    showError("L'angle doit être entre 0 et 360 degrés");
                    return;
                }
        
                creerEchiquier(nbPions, nomJ1, nomJ2, joueurPremier, angle);
            } catch (NumberFormatException ex) {
                showError("Veuillez entrer des nombres valides");
            }
        });

        HBox line1 = new HBox(10);
        line1.setAlignment(Pos.CENTER);
        line1.getChildren().addAll(labelNbPions, textFieldNbPions, labelNomJ1, textFieldNomJ1, labelNomJ2, textFieldNomJ2);
        
        HBox line2 = new HBox(10);
        line2.setAlignment(Pos.CENTER);
        line2.getChildren().addAll(labelPremier, comboPremier, labelAngle, textFieldAngle, btnCreer);
        
        vbox.getChildren().addAll(titleLabel, line1, line2, infoLabel);

        return vbox;
    }

    private VBox createVieConfigPanel() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");
        vbox.setPrefWidth(200);

        Label titleLabel = new Label("Configuration des Vies");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Roi
        VBox roiBox = createVieField("♔ Roi:", "100");
        txtVieRoi = (TextField) roiBox.getChildren().get(1);

        // Reine
        VBox reineBox = createVieField("♕ Reine:", "90");
        txtVieReine = (TextField) reineBox.getChildren().get(1);

        // Tour
        VBox tourBox = createVieField("♖ Tour:", "50");
        txtVieTour = (TextField) tourBox.getChildren().get(1);

        // Fou
        VBox fouBox = createVieField("♗ Fou:", "30");
        txtVieFou = (TextField) fouBox.getChildren().get(1);

        // Cavalier
        VBox cavalierBox = createVieField("♘ Cavalier:", "30");
        txtVieCavalier = (TextField) cavalierBox.getChildren().get(1);

        // Pion
        VBox pionBox = createVieField("♙ Pion:", "10");
        txtViePion = (TextField) pionBox.getChildren().get(1);

        // Bouton pour réinitialiser les valeurs par défaut
        Button btnReset = new Button("Valeurs par défaut");
        btnReset.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnReset.setOnAction(e -> resetViesParDefaut());

        Label infoLabel = new Label("Modifiez les points de vie de chaque type de pièce");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        infoLabel.setMaxWidth(180);

        vbox.getChildren().addAll(
            titleLabel,
            new javafx.scene.control.Separator(),
            roiBox,
            reineBox,
            tourBox,
            fouBox,
            cavalierBox,
            pionBox,
            new javafx.scene.control.Separator(),
            btnReset,
            infoLabel
        );

        return vbox;
    }

    private VBox createVieField(String label, String defaultValue) {
        VBox box = new VBox(5);
        
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        TextField txt = new TextField(defaultValue);
        txt.setPrefWidth(160);
        txt.setPromptText("Vie");
        
        box.getChildren().addAll(lbl, txt);
        return box;
    }

    private void resetViesParDefaut() {
        txtVieRoi.setText("100");
        txtVieReine.setText("90");
        txtVieTour.setText("50");
        txtVieFou.setText("30");
        txtVieCavalier.setText("30");
        txtViePion.setText("10");
    }

    private void creerEchiquier(int nbPions, String nomJ1, String nomJ2, int joueurPremier, double angle) {
        joueur1 = new Joueur(nomJ1);
        joueur2 = new Joueur(nomJ2);
        
        try {
            int vieRoi = Integer.parseInt(txtVieRoi.getText());
            int vieReine = Integer.parseInt(txtVieReine.getText());
            int vieTour = Integer.parseInt(txtVieTour.getText());
            int vieFou = Integer.parseInt(txtVieFou.getText());
            int vieCavalier = Integer.parseInt(txtVieCavalier.getText());
            int viePion = Integer.parseInt(txtViePion.getText());
            
            if (vieRoi <= 0 || vieReine <= 0 || vieTour <= 0 || vieFou <= 0 || vieCavalier <= 0 || viePion <= 0) {
                showError("Toutes les vies doivent être supérieures à 0");
                return;
            }
            
            if (modeMultijoueur && isServeur) {
                // Envoyer la configuration au client
                ConfigurationJeu config = new ConfigurationJeu(nbPions, nomJ1, nomJ2, joueurPremier, 
                    angle, vieRoi, vieReine, vieTour, vieFou, vieCavalier, viePion);
                serveur.envoyerMessage(new MessageReseau(MessageReseau.TypeMessage.CONFIGURATION_JEU, config));
            }
            
            echiquier = new Echiquier(joueur1, joueur2, nbPions, joueurPremier, angle, 
                                     vieRoi, vieReine, vieTour, vieFou, vieCavalier, viePion);
        } catch (NumberFormatException ex) {
            showError("Veuillez entrer des valeurs numériques valides pour les vies");
            return;
        }

        if (echiquierView != null) {
            echiquierView.arreterAnimation();
        }

        echiquierView = new EchiquierView(echiquier, modeMultijoueur, isServeur, serveur, client);
        root.setCenter(echiquierView);
        
        VBox bottomPanel = createBottomPanel(joueur1, joueur2);
        root.setBottom(bottomPanel);
        
        demarrerMiseAJour();
        
        javafx.application.Platform.runLater(() -> {
            echiquierView.requestFocus();
        });
    }

    private void demarrerJeuMultijoueur(ConfigurationJeu config) {
        joueur1 = new Joueur(config.getNomJoueur1());
        joueur2 = new Joueur(config.getNomJoueur2());
        
        echiquier = new Echiquier(joueur1, joueur2, config.getNbPions(), config.getJoueurPremier(), 
                                 config.getAngle(), config.getVieRoi(), config.getVieReine(), 
                                 config.getVieTour(), config.getVieFou(), config.getVieCavalier(), 
                                 config.getViePion());

        if (echiquierView != null) {
            echiquierView.arreterAnimation();
        }

        echiquierView = new EchiquierView(echiquier, modeMultijoueur, isServeur, serveur, client);
        root.setCenter(echiquierView);
        
        VBox bottomPanel = createBottomPanel(joueur1, joueur2);
        root.setBottom(bottomPanel);
        
        demarrerMiseAJour();
        
        javafx.application.Platform.runLater(() -> {
            echiquierView.requestFocus();
        });
    }

    private void demarrerMiseAJour() {
        javafx.animation.AnimationTimer updateTimer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (scoreLabel != null && joueur1 != null && joueur2 != null) {
                    scoreLabel.setText(String.format("%s: %d points | %s: %d points", 
                        joueur1.getNom(), joueur1.getPoint(),
                        joueur2.getNom(), joueur2.getPoint()));
                }
                if (vitesseLabel != null && echiquier != null) {
                    vitesseLabel.setText(String.format("Vitesse: %.2f", echiquier.getBalle().getVitesse()));
                }
            }
        };
        updateTimer.start();
    }

    private VBox createBottomPanel(Joueur joueur1, Joueur joueur2) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #e0e0e0;");

        // Panneau de score
        scoreLabel = new Label(String.format("%s: %d points | %s: %d points", 
            joueur1.getNom(), joueur1.getPoint(),
            joueur2.getNom(), joueur2.getPoint()));
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Panneau de contrôle de vitesse (seulement pour le serveur en mode multijoueur)
        if (!modeMultijoueur || isServeur) {
            HBox vitesseBox = new HBox(10);
            vitesseBox.setAlignment(Pos.CENTER);

            vitesseLabel = new Label(String.format("Vitesse: %.2f", echiquier.getBalle().getVitesse()));
            vitesseLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

            Button btnMoinsVitesse = new Button("-");
            btnMoinsVitesse.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 40px;");
            btnMoinsVitesse.setOnAction(e -> {
                echiquier.diminuerVitesseBalle(0.1);
                if (modeMultijoueur && isServeur) {
                    envoyerChangementVitesse(echiquier.getBalle().getVitesse());
                }
            });

            Button btnPlusVitesse = new Button("+");
            btnPlusVitesse.setStyle("-fx-background-color: #4ECDC4; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 40px;");
            btnPlusVitesse.setOnAction(e -> {
                echiquier.augmenterVitesseBalle(0.1);
                if (modeMultijoueur && isServeur) {
                    envoyerChangementVitesse(echiquier.getBalle().getVitesse());
                }
            });

            Button btnMoinsVitesseRapide = new Button("--");
            btnMoinsVitesseRapide.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 40px;");
            btnMoinsVitesseRapide.setOnAction(e -> {
                echiquier.diminuerVitesseBalle(0.5);
                if (modeMultijoueur && isServeur) {
                    envoyerChangementVitesse(echiquier.getBalle().getVitesse());
                }
            });

            Button btnPlusVitesseRapide = new Button("++");
            btnPlusVitesseRapide.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 40px;");
            btnPlusVitesseRapide.setOnAction(e -> {
                echiquier.augmenterVitesseBalle(0.5);
                if (modeMultijoueur && isServeur) {
                    envoyerChangementVitesse(echiquier.getBalle().getVitesse());
                }
            });

            vitesseBox.getChildren().addAll(
                new Label("Contrôle vitesse:"),
                btnMoinsVitesseRapide,
                btnMoinsVitesse,
                vitesseLabel,
                btnPlusVitesse,
                btnPlusVitesseRapide
            );

            vbox.getChildren().addAll(scoreLabel, vitesseBox);
        } else {
            // Client en mode multijoueur - affichage seul
            vitesseLabel = new Label(String.format("Vitesse: %.2f", echiquier.getBalle().getVitesse()));
            vitesseLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            vbox.getChildren().addAll(scoreLabel, vitesseLabel);
        }

        return vbox;
    }

    private void envoyerChangementVitesse(double nouvelleVitesse) {
        MessageReseau msg = new MessageReseau(MessageReseau.TypeMessage.CHANGEMENT_VITESSE, nouvelleVitesse);
        if (isServeur && serveur != null) {
            serveur.envoyerMessage(msg);
        } else if (client != null) {
            client.envoyerMessage(msg);
        }
    }

    private void showError(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        
        VBox topPanel = (VBox) root.getTop();
        if (topPanel.getChildren().size() > 3) {
            topPanel.getChildren().remove(3);
        }
        topPanel.getChildren().add(errorLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
