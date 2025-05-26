package com.example.treasurehuntgame.game.ui;

import com.example.treasurehuntgame.game.entities.Enemy;
import com.example.treasurehuntgame.game.entities.Player;
import com.example.treasurehuntgame.game.entities.Trap;
import com.example.treasurehuntgame.game.entities.Treasure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
        gc.setFill(Color.SANDYBROWN);
        gc.fillRect(0, 0, gameBoard.getGridWidth() * gameBoard.getCellSize(), gameBoard.getGridHeight() * gameBoard.getCellSize());

        gc.setStroke(Color.BROWN);
        gc.setLineWidth(1);
        for (int x = 0; x <= gameBoard.getGridWidth(); x++) {
            gc.strokeLine(x * gameBoard.getCellSize(), 0, x * gameBoard.getCellSize(), gameBoard.getGridHeight() * gameBoard.getCellSize());
        }
        for (int y = 0; y <= gameBoard.getGridHeight(); y++) {
            gc.strokeLine(0, y * gameBoard.getCellSize(), gameBoard.getGridWidth() * gameBoard.getCellSize(), y * gameBoard.getCellSize());
        }

        gc.setFill(Color.DARKGRAY);
        for (int x = 0; x < gameBoard.getGridWidth(); x++) {
            for (int y = 0; y < gameBoard.getGridHeight(); y++) {
                if (gameBoard.getWalls()[x][y]) {
                    gc.fillRect(x * gameBoard.getCellSize(), y * gameBoard.getCellSize(), gameBoard.getCellSize(), gameBoard.getCellSize());
                }
            }
        }

        gc.setFill(Color.GOLD);
        Treasure treasure = gameBoard.getTreasure();
        gc.fillOval(treasure.x * gameBoard.getCellSize() + 2, treasure.y * gameBoard.getCellSize() + 2, gameBoard.getCellSize() - 4, gameBoard.getCellSize() - 4);

        gc.setFill(Color.RED);
        for (Trap trap : gameBoard.getTraps()) {
            if (!trap.triggered) {
                gc.fillRect(trap.x * gameBoard.getCellSize() + 4, trap.y * gameBoard.getCellSize() + 4, gameBoard.getCellSize() - 8, gameBoard.getCellSize() - 8);
            }
        }

        gc.setFill(Color.PURPLE);
        for (Enemy enemy : gameBoard.getEnemies()) {
            gc.fillOval(enemy.x * gameBoard.getCellSize() + 1, enemy.y * gameBoard.getCellSize() + 1, gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
        }

        gc.setFill(Color.BLUE);
        Player player = gameBoard.getPlayer();
        gc.fillOval(player.x * gameBoard.getCellSize() + 1, player.y * gameBoard.getCellSize() + 1, gameBoard.getCellSize() - 2, gameBoard.getCellSize() - 2);
    }
}