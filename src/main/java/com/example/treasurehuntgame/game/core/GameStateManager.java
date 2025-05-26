package com.example.treasurehuntgame.game.core;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.database.DatabaseManager;
import com.example.treasurehuntgame.game.entities.Enemy;
import com.example.treasurehuntgame.game.entities.Trap;
import com.example.treasurehuntgame.game.ui.GameBoard;
import com.example.treasurehuntgame.game.ui.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class GameStateManager {
    private final GameDifficulty difficulty;
    private final String playerName;
    private final TreasureHuntGame mainApp;
    private final DatabaseManager dbManager;
    private final GameBoard gameBoard;
    private boolean gameRunning = true;
    private boolean gameWon = false;
    private long gameStartTime;
    private int playerLives;
    private long timeLimit;
    private boolean randomMovement = false;
    private AnimationTimer gameLoop;

    public GameStateManager(GameDifficulty difficulty, String playerName, TreasureHuntGame mainApp, DatabaseManager dbManager, GameBoard gameBoard) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.mainApp = mainApp;
        this.dbManager = dbManager;
        this.gameBoard = gameBoard;
        switch (difficulty) {
            case EASY:
                playerLives = 5;
                timeLimit = 0;
                break;
            case MEDIUM:
                playerLives = 3;
                timeLimit = 0;
                break;
            case HARD:
                playerLives = 2;
                timeLimit = 120000;
                break;
        }
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public void startGameLoop(GameRenderer gameRenderer, GameBoard gameBoard) {
        gameLoop = new AnimationTimer() {
            private long lastEnemyMove = 0;

            @Override
            public void handle(long now) {
                if (!gameRunning) {
                    stop();
                    return;
                }

                if (timeLimit > 0 && System.currentTimeMillis() - gameStartTime > timeLimit) {
                    endGame(false);
                    stop();
                    return;
                }

                if (now - lastEnemyMove > 500_000_000) {
                    gameBoard.moveEnemies();
                    lastEnemyMove = now;
                }

                gameRenderer.render();
            }
        };
        gameLoop.start();
    }

    public void movePlayer(int newX, int newY) {
        gameBoard.movePlayer(newX, newY);

        if (gameBoard.getPlayer().x == gameBoard.getTreasure().x && gameBoard.getPlayer().y == gameBoard.getTreasure().y) {
            gameWon = true;
            endGame(true);
            return;
        }

        for (Trap trap : gameBoard.getTraps()) {
            if (gameBoard.getPlayer().x == trap.x && gameBoard.getPlayer().y == trap.y && !trap.triggered) {
                trap.triggered = true;
                playerLives--;
                if (playerLives <= 0) {
                    endGame(false);
                    return;
                }
            }
        }

        for (Enemy enemy : gameBoard.getEnemies()) {
            if (Math.abs(gameBoard.getPlayer().x - enemy.x) <= 1 && Math.abs(gameBoard.getPlayer().y - enemy.y) <= 1) {
                playerLives--;
                if (playerLives <= 0) {
                    endGame(false);
                    return;
                }
            }
        }
    }

    public void updateInfoPanel(HBox infoPanel) {
        Label livesLabel = (Label) infoPanel.getChildren().get(0);
        livesLabel.setText("Lives: " + playerLives);

        if (timeLimit > 0) {
            Label timeLabel = (Label) infoPanel.getChildren().get(2);
            long remainingTime = Math.max(0, timeLimit - (System.currentTimeMillis() - gameStartTime));
            timeLabel.setText("Time: " + (remainingTime / 1000) + "s");
        }
    }

    public void endGame(boolean won) {
        gameRunning = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }

        long gameEndTime = System.currentTimeMillis();
        int timeSpent = (int) ((gameEndTime - gameStartTime) / 1000);

        dbManager.saveGameResult(playerName, difficulty.toString(), won ? "WIN" : "LOSS", timeSpent);

        Alert alert = new Alert(won ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        alert.setTitle("Game Over");
        alert.setHeaderText(won ? "🏆 Congratulations!" : "💀 Game Over!");
        alert.setContentText(won ?
                "You found the treasure in " + timeSpent + " seconds!" :
                "Better luck next time! Time spent: " + timeSpent + " seconds");

        ButtonType playAgainButton = new ButtonType("Play Again");
        ButtonType menuButton = new ButtonType("Main Menu");
        alert.getButtonTypes().setAll(playAgainButton, menuButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == playAgainButton) {
                mainApp.startGame(difficulty);
            } else {
                mainApp.returnToMainMenu();
            }
        }
    }

    public void toggleRandomMovement() {
        randomMovement = !randomMovement;
    }

    public boolean isGameRunning() { return gameRunning; }
    public boolean isRandomMovement() { return randomMovement; }
    public GameDifficulty getDifficulty() { return difficulty; }
    public int getPlayerLives() { return playerLives; }
    public long getTimeLimit() { return timeLimit; }
}