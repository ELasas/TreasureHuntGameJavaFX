package com.example.treasurehuntgame.scenes;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.database.DatabaseManager;
import com.example.treasurehuntgame.game.core.GameDifficulty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

public class MainMenuScene {
    private final TreasureHuntGame app;
    private final DatabaseManager dbManager;
    private final Scene scene;
    private String currentPlayer;
    private Label welcomeLabel;

    public MainMenuScene(TreasureHuntGame app, DatabaseManager dbManager) {
        this.app = app;
        this.dbManager = dbManager;
        this.scene = createMainMenuScene();
    }

    private Scene createMainMenuScene() {
        VBox menuLayout = new VBox(25);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(50));
        menuLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1c2526, #2f4f4f);");
        menuLayout.getStyleClass().add("vbox");

        welcomeLabel = new Label("Welcome, " + currentPlayer + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.GOLD);
        welcomeLabel.getStyleClass().add("welcome");

        Label titleLabel = new Label("🏴‍☠️ TREASURE HUNT 🏴‍☠️");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.getStyleClass().add("title");

        Button easyButton = new Button("🟢 EASY LEVEL");
        easyButton.getStyleClass().addAll("button", "menu-button", "easy-button");

        Button mediumButton = new Button("🟡 MEDIUM LEVEL");
        mediumButton.getStyleClass().addAll("button", "menu-button", "medium-button");

        Button hardButton = new Button("🔴 HARD LEVEL");
        hardButton.getStyleClass().addAll("button", "menu-button", "hard-button");

        Button historyButton = new Button("📊 VIEW GAME HISTORY");
        historyButton.getStyleClass().addAll("button", "menu-button", "history-button");

        Button logoutButton = new Button("🚪 LOGOUT");
        logoutButton.getStyleClass().addAll("button", "menu-button", "logout-button");

        easyButton.setOnAction(e -> app.startGame(GameDifficulty.EASY));
        mediumButton.setOnAction(e -> app.startGame(GameDifficulty.MEDIUM));
        hardButton.setOnAction(e -> app.startGame(GameDifficulty.HARD));
        historyButton.setOnAction(e -> app.showGameHistory());
        logoutButton.setOnAction(e -> app.showLoginScene());

        VBox difficultyBox = new VBox(15);
        difficultyBox.setAlignment(Pos.CENTER);
        difficultyBox.getStyleClass().add("vbox");
        difficultyBox.getChildren().addAll(
                new Label("Choose your difficulty:") {{
                    setTextFill(Color.WHITE);
                    setFont(Font.font("Arial", 20));
                    getStyleClass().add("label");
                }},
                easyButton, mediumButton, hardButton
        );

        menuLayout.getChildren().addAll(
                titleLabel,
                welcomeLabel,
                difficultyBox,
                historyButton,
                logoutButton
        );

        Scene menuScene = new Scene(menuLayout, 800, 800);
        menuScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        return menuScene;
    }

    public void setCurrentPlayer(String player) {
        this.currentPlayer = player;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + currentPlayer + "!");
        }
    }

    public Scene getScene() {
        return scene;
    }
}