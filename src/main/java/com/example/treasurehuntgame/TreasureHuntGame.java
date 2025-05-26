package com.example.treasurehuntgame;

import com.example.treasurehuntgame.database.DatabaseManager;
import com.example.treasurehuntgame.game.core.GameDifficulty;
import com.example.treasurehuntgame.game.core.GameEngine;
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

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.dbManager = new DatabaseManager();
        dbManager.initializeTables();

        primaryStage.setTitle("Treasure Hunt Game");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);

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
    }

    public void showRegistrationScene() {
        primaryStage.setScene(registrationScene.getScene());
    }

    public void showMainMenu() {
        mainMenuScene.setCurrentPlayer(currentPlayer);
        primaryStage.setScene(mainMenuScene.getScene());
    }

    public void showGameHistory() {
        gameHistoryScene.setCurrentPlayer(currentPlayer);
        primaryStage.setScene(gameHistoryScene.getScene());
    }

    public void startGame(GameDifficulty difficulty) {
        GameEngine gameEngine = new GameEngine(difficulty, currentPlayer, this);
        gameEngine.startGame();
    }

    public void returnToMainMenu() { showMainMenu(); }

    public void setCurrentPlayer(String player) {
        this.currentPlayer = player;
    }
}
