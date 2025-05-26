
package com.example.treasurehuntgame.game.ui;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.game.core.GameStateManager;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameUI {
    private final TreasureHuntGame mainApp;
    private final GameBoard gameBoard;
    private final GameRenderer gameRenderer;
    private final GameStateManager gameStateManager;
    private Stage gameStage;

    public GameUI(TreasureHuntGame mainApp, GameBoard gameBoard, GameRenderer gameRenderer, GameStateManager gameStateManager) {
        this.mainApp = mainApp;
        this.gameBoard = gameBoard;
        this.gameRenderer = gameRenderer;
        this.gameStateManager = gameStateManager;
    }

    public void createGameStage() {
        gameStage = new Stage();
        gameStage.setTitle("Treasure Hunt - " + gameStateManager.getDifficulty());
        gameStage.setResizable(false);

        Pane gamePane = new Pane();
        Canvas canvas = new Canvas(gameBoard.getGridWidth() * gameBoard.getCellSize(), gameBoard.getGridHeight() * gameBoard.getCellSize());
        gamePane.getChildren().add(canvas);
        gameRenderer.setGraphicsContext(canvas.getGraphicsContext2D());

        Scene gameScene = new Scene(gamePane, gameBoard.getGridWidth() * gameBoard.getCellSize(), gameBoard.getGridHeight() * gameBoard.getCellSize());
        gameStage.setScene(gameScene);

        gameScene.setOnKeyPressed(event -> {
            int newX = gameBoard.getPlayer().x;
            int newY = gameBoard.getPlayer().y;
            if (event.getCode() == KeyCode.UP) {
                newY--;
            } else if (event.getCode() == KeyCode.DOWN) {
                newY++;
            } else if (event.getCode() == KeyCode.LEFT) {
                newX--;
            } else if (event.getCode() == KeyCode.RIGHT) {
                newX++;
            }
            gameStateManager.movePlayer(newX, newY);
            gameRenderer.render();
        });

        gameStage.show();
        gameRenderer.render();
    }

    public void closeGameStage() {
        if (gameStage != null) {
            gameStage.close();
            gameStage = null;
        }
    }
}