package com.example.treasurehuntgame.game.ui;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.game.core.GameStateManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Objects;

public class GameUI {
    private final TreasureHuntGame mainApp;
    private final GameBoard gameBoard;
    private final GameRenderer gameRenderer;
    private final GameStateManager gameStateManager;
    private Stage gameStage;
    private Canvas canvas;
    private Label controlsLabel;

    public GameUI(TreasureHuntGame mainApp, GameBoard gameBoard, GameRenderer gameRenderer, GameStateManager gameStateManager) {
        this.mainApp = mainApp;
        this.gameBoard = gameBoard;
        this.gameRenderer = gameRenderer;
        this.gameStateManager = gameStateManager;
    }

    public void createGameStage() {
        gameStage = new Stage();
        gameStage.setTitle("Treasure Hunt - " + gameStateManager.getDifficulty() + " Level");
        gameStage.setResizable(true);
        gameStage.setMinWidth(600);
        gameStage.setMinHeight(500);

        VBox gameLayout = new VBox(10);
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.setPadding(new Insets(10));
        gameLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #3c2f2f, #5c4033);");
        gameLayout.getStyleClass().add("vbox");

        HBox infoPanel = new HBox(20);
        infoPanel.setAlignment(Pos.CENTER);
        infoPanel.setPadding(new Insets(10));
        infoPanel.getStyleClass().add("info-panel");

        Label livesLabel = new Label("Lives: " + gameStateManager.getPlayerLives());
        livesLabel.setTextFill(Color.WHITE);
        livesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        livesLabel.getStyleClass().add("label");

        Label difficultyLabel = new Label("Difficulty: " + gameStateManager.getDifficulty());
        difficultyLabel.setTextFill(Color.GOLD);
        difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        difficultyLabel.getStyleClass().add("label");

        Label timeLabel = new Label();
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        timeLabel.getStyleClass().add("label");

        if (gameStateManager.getTimeLimit() > 0) {
            timeLabel.setText("Time: " + (gameStateManager.getTimeLimit() / 1000) + "s");
        } else {
            timeLabel.setText("Time: ∞");
        }

        infoPanel.getChildren().addAll(livesLabel, difficultyLabel, timeLabel);

        canvas = new Canvas(gameBoard.getGridWidth() * gameBoard.getCellSize(), gameBoard.getGridHeight() * gameBoard.getCellSize());
        gameRenderer.setGraphicsContext(canvas.getGraphicsContext2D());

        controlsLabel = new Label("Use ARROW KEYS/WASD to move | Press 'R' for Random Movement | Find the 💰 treasure!");
        controlsLabel.setTextFill(Color.WHITE);
        controlsLabel.setFont(Font.font("Arial", 14));
        controlsLabel.getStyleClass().add("label");

        Button menuButton = new Button("🏠 BACK TO MENU");
        menuButton.getStyleClass().addAll("button", "menu-back-button");
        menuButton.setOnAction(e -> {
            gameStateManager.endGame(false);
            gameStage.close();
            mainApp.returnToMainMenu();
        });

        gameLayout.getChildren().addAll(infoPanel, canvas, controlsLabel, menuButton);

        Scene gameScene = new Scene(gameLayout);
        gameScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        gameScene.setOnKeyPressed(e -> {
            if (!gameStateManager.isGameRunning()) return;

            KeyCode key = e.getCode();
            int newX = gameBoard.getPlayer().x;
            int newY = gameBoard.getPlayer().y;

            if (key == KeyCode.R) {
                gameStateManager.toggleRandomMovement();
                controlsLabel.setText("Random Movement: " + (gameStateManager.isRandomMovement() ? "ON" : "OFF") + " | Use ARROW KEYS/WASD to move manually | Find the 💰 treasure!");
                return;
            }

            if (!gameStateManager.isRandomMovement()) {
                switch (key) {
                    case UP: case W: newY--; break;
                    case DOWN: case S: newY++; break;
                    case LEFT: case A: newX--; break;
                    case RIGHT: case D: newX++; break;
                    default: return;
                }
                gameStateManager.movePlayer(newX, newY);
            } else {
                java.util.Random rand = new java.util.Random();
                int[] directions = {-1, 0, 1};
                int dx = directions[rand.nextInt(3)];
                int dy = directions[rand.nextInt(3)];
                newX = gameBoard.getPlayer().x + dx;
                newY = gameBoard.getPlayer().y + dy;
                gameStateManager.movePlayer(newX, newY);
            }
            gameStateManager.updateInfoPanel(infoPanel);
        });

        gameStage.setScene(gameScene);
        gameStage.show();
        gameScene.getRoot().requestFocus();
    }

    public Canvas getCanvas() { return canvas; }
    public Stage getGameStage() { return gameStage; }
}