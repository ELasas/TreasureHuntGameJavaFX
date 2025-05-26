package com.example.treasurehuntgame;

import com.example.treasurehuntgame.database.DatabaseManager;
import com.example.treasurehuntgame.game.core.GameDifficulty;
import com.example.treasurehuntgame.game.core.GameEngine;
import com.example.treasurehuntgame.game.ui.GameUI;
import com.example.treasurehuntgame.scenes.GameHistoryScene;
import com.example.treasurehuntgame.scenes.LoginScene;
import com.example.treasurehuntgame.scenes.MainMenuScene;
import com.example.treasurehuntgame.scenes.RegistrationScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class TreasureHuntGame extends Application {
    private Stage primaryStage;
    private String currentPlayer;
    private DatabaseManager dbManager;
    private LoginScene loginScene;
    private RegistrationScene registrationScene;
    private MainMenuScene mainMenuScene;
    private GameHistoryScene gameHistoryScene;
    private GameUI gameUI;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.dbManager = new DatabaseManager();
        dbManager.initializeTables();

        primaryStage.setTitle("Treasure Hunt Game");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(800);
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);

        loginScene = new LoginScene(this, dbManager);
        registrationScene = new RegistrationScene(this, dbManager);
        mainMenuScene = new MainMenuScene(this, dbManager);
        gameHistoryScene = new GameHistoryScene(this, dbManager);

        showLoginScene();
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

    public void showLoginScene() {
        primaryStage.setScene(loginScene.getScene());
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);
    }

    public void showRegistrationScene() {
        primaryStage.setScene(registrationScene.getScene());
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);
    }

    public void showMainMenu() {
        mainMenuScene.setCurrentPlayer(currentPlayer);
        primaryStage.setScene(mainMenuScene.getScene());
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);
        if (gameUI != null) {
            gameUI.closeGameStage();
            gameUI = null;
        }
    }

    public void showGameHistory() {
        gameHistoryScene.setCurrentPlayer(currentPlayer);
        primaryStage.setScene(gameHistoryScene.getScene());
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);
    }

    public void startGame(GameDifficulty difficulty) {
        GameEngine gameEngine = new GameEngine(difficulty, currentPlayer, this);
        gameEngine.startGame();
        this.gameUI = gameEngine.getGameUI();
    }

    public void returnToMainMenu() {
        showMainMenu();
    }

    public void setCurrentPlayer(String player) {
        this.currentPlayer = player;
    }
}