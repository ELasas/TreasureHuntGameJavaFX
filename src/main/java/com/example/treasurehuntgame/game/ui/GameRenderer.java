
package com.example.treasurehuntgame.game.ui;

import com.example.treasurehuntgame.game.entities.Enemy;
import com.example.treasurehuntgame.game.entities.Player;
import com.example.treasurehuntgame.game.entities.Trap;
import com.example.treasurehuntgame.game.entities.Treasure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class GameRenderer {
    private final GameBoard gameBoard;
    private GraphicsContext gc;

    public GameRenderer(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }

    public void render() {
        if (gc == null) {
            System.err.println("Error: GraphicsContext is not set!");
            return;
        }

        gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.SANDYBROWN), new Stop(1, Color.BURLYWOOD)));
        gc.fillRect(0, 0, gameBoard.getGridWidth() * gameBoard.getCellSize(), gameBoard.getGridHeight() * gameBoard.getCellSize());

        gc.setStroke(Color.BROWN);
        gc.setLineWidth(0.5);
        for (int x = 0; x <= gameBoard.getGridWidth(); x++) {
            gc.strokeLine(x * gameBoard.getCellSize(), 0, x * gameBoard.getCellSize(), gameBoard.getGridHeight() * gameBoard.getCellSize());
        }
        for (int y = 0; y <= gameBoard.getGridHeight(); y++) {
            gc.strokeLine(0, y * gameBoard.getCellSize(), gameBoard.getGridWidth() * gameBoard.getCellSize(), y * gameBoard.getCellSize());
        }

        if (gameBoard.getWalls() != null) {
            gc.setFill(Color.DARKSLATEGRAY);
            for (int x = 0; x < gameBoard.getGridWidth(); x++) {
                for (int y = 0; y < gameBoard.getGridHeight(); y++) {
                    if (gameBoard.getWalls()[x][y]) {
                        gc.fillRect(x * gameBoard.getCellSize() + 1, y * gameBoard.getCellSize() + 1,
                                gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(x * gameBoard.getCellSize() + 1, y * gameBoard.getCellSize() + 1,
                                gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
                    }
                }
            }
        }

        Treasure treasure = gameBoard.getTreasure();
        if (treasure != null) {
            gc.setFill(Color.GOLD);
            gc.fillOval(treasure.x * gameBoard.getCellSize() + 2, treasure.y * gameBoard.getCellSize() + 2,
                    gameBoard.getCellSize() - 4, gameBoard.getCellSize() - 4);
            gc.setStroke(Color.DARKGOLDENROD);
            gc.strokeOval(treasure.x * gameBoard.getCellSize() + 2, treasure.y * gameBoard.getCellSize() + 2,
                    gameBoard.getCellSize() - 4, gameBoard.getCellSize() - 4);
        }

        if (gameBoard.getTraps() != null) {
            gc.setFill(Color.CRIMSON);
            for (Trap trap : gameBoard.getTraps()) {
                if (!trap.triggered) {
                    gc.fillRect(trap.x * gameBoard.getCellSize() + 4, trap.y * gameBoard.getCellSize() + 4,
                            gameBoard.getCellSize() - 8, gameBoard.getCellSize() - 8);
                    gc.setStroke(Color.DARKRED);
                    gc.strokeRect(trap.x * gameBoard.getCellSize() + 4, trap.y * gameBoard.getCellSize() + 4,
                            gameBoard.getCellSize() - 8, gameBoard.getCellSize() - 8);
                }
            }
        }

        if (gameBoard.getEnemies() != null) {
            gc.setFill(Color.DARKVIOLET);
            for (Enemy enemy : gameBoard.getEnemies()) {
                gc.fillOval(enemy.x * gameBoard.getCellSize() + 1, enemy.y * gameBoard.getCellSize() + 1,
                        gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(enemy.x * gameBoard.getCellSize() + 1, enemy.y * gameBoard.getCellSize() + 1,
                        gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
            }
        }

        Player player = gameBoard.getPlayer();
        if (player != null) {
            gc.setFill(Color.DODGERBLUE);
            gc.fillOval(player.x * gameBoard.getCellSize() + 1, player.y * gameBoard.getCellSize() + 1,
                    gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
            gc.setStroke(Color.DARKBLUE);
            gc.strokeOval(player.x * gameBoard.getCellSize() + 1, player.y * gameBoard.getCellSize() + 1,
                    gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
        }
    }
}