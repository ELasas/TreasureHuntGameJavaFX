package com.example.treasurehuntgame.game.core;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.database.DatabaseManager;
import com.example.treasurehuntgame.game.ui.GameBoard;
import com.example.treasurehuntgame.game.ui.GameRenderer;
import com.example.treasurehuntgame.game.ui.GameUI;

public class GameEngine {
    private final GameDifficulty difficulty;
    private final String playerName;
    private final TreasureHuntGame mainApp;
    private final DatabaseManager dbManager;
    private GameBoard gameBoard;
    private GameRenderer gameRenderer;
    private GameUI gameUI;
    private GameStateManager gameStateManager;

    public GameEngine(GameDifficulty difficulty, String playerName, TreasureHuntGame mainApp) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.mainApp = mainApp;
        this.dbManager = new DatabaseManager();
    }

    public void startGame() {
        gameBoard = new GameBoard(difficulty);
        gameBoard.initializeGame();
        gameRenderer = new GameRenderer(gameBoard);
        gameStateManager = new GameStateManager(difficulty, playerName, mainApp, dbManager, gameBoard);
        gameUI = new GameUI(mainApp, gameBoard, gameRenderer, gameStateManager);
        gameStateManager.setGameStartTime(System.currentTimeMillis());
        gameUI.createGameStage();
        startGameLoop();
    }

    private void startGameLoop() {
        gameStateManager.startGameLoop(gameRenderer, gameBoard);
    }
}