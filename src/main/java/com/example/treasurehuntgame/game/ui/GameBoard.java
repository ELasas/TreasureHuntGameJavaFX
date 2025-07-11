package com.example.treasurehuntgame.game.ui;

import com.example.treasurehuntgame.game.core.GameDifficulty;
import com.example.treasurehuntgame.game.entities.Enemy;
import com.example.treasurehuntgame.game.entities.Player;
import com.example.treasurehuntgame.game.entities.Trap;
import com.example.treasurehuntgame.game.entities.Treasure;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private final GameDifficulty difficulty;
    private Player player;
    private Treasure treasure;
    private List<Enemy> enemies;
    private List<Trap> traps;
    private boolean[][] walls;
    private int gridWidth;
    private int gridHeight;
    private final int cellSize = 40;

    public GameBoard(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        this.enemies = new ArrayList<>();
        this.traps = new ArrayList<>();
        switch (difficulty) {
            case EASY:
                gridWidth = 10;
                gridHeight = 10;
                break;
            case MEDIUM:
                gridWidth = 15;
                gridHeight = 15;
                break;
            case HARD:
                gridWidth = 20;
                gridHeight = 20;
                break;
        }
        this.walls = new boolean[gridWidth][gridHeight];
    }

    public void initializeGame() {
        switch (difficulty) {
            case EASY:
                player = new Player(0, 0);
                treasure = new Treasure(9, 9);
                enemies.clear();
                enemies.add(new Enemy(5, 5));
                traps.clear();
                traps.add(new Trap(3, 3));
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        walls[x][y] = (y == 4 && x >= 2 && x <= 7 && x != 5) ||
                                (y == 6 && x >= 3 && x <= 8 && x != 6) ||
                                (x == 5 && (y == 2 || y == 3 || y == 7)) ||
                                (x == 3 && (y == 5)) ||
                                (x == 7 && (y == 5));
                    }
                }
                break;
            case MEDIUM:
                player = new Player(0, 0);
                treasure = new Treasure(14, 14);
                enemies.clear();
                enemies.add(new Enemy(7, 7));
                enemies.add(new Enemy(10, 10));
                traps.clear();
                traps.add(new Trap(5, 5));
                traps.add(new Trap(8, 8));
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        walls[x][y] = (y == 6 && x >= 3 && x <= 12 && x != 7) ||
                                (y == 8 && x >= 4 && x <= 13 && x != 9) ||
                                (y == 10 && x >= 2 && x <= 11 && x != 6) ||
                                (x == 7 && (y == 3 || y == 5 || y == 11)) ||
                                (x == 8 && (y == 4 || y == 7));
                    }
                }
                break;
            case HARD:
                player = new Player(0, 0);
                treasure = new Treasure(19, 19);
                enemies.clear();
                enemies.add(new Enemy(10, 10));
                enemies.add(new Enemy(15, 15));
                enemies.add(new Enemy(5, 5));
                traps.clear();
                traps.add(new Trap(7, 7));
                traps.add(new Trap(12, 12));
                traps.add(new Trap(17, 17));
                for (int x = 0; x < gridWidth; x++) {
                    for (int y = 0; y < gridHeight; y++) {
                        walls[x][y] = (y == 8 && x >= 5 && x <= 15 && x != 10) ||
                                (y == 10 && x >= 4 && x <= 16 && x != 8) ||
                                (y == 12 && x >= 6 && x <= 17 && x != 9) ||
                                (y == 14 && x >= 3 && x <= 14 && x != 11) ||
                                (x == 10 && (y == 3 || y == 5 || y == 7)) ||
                                (x == 11 && (y == 4 || y == 6 || y == 9));
                    }
                }
                break;
        }
    }

    public void movePlayer(int newX, int newY) {
        if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight && !walls[newX][newY]) {
            player.x = newX;
            player.y = newY;
        }
    }

    public void moveEnemies() {
        for (Enemy enemy : enemies) {
            int dx = player.x - enemy.x;
            int dy = player.y - enemy.y;
            int newX = enemy.x;
            int newY = enemy.y;
            if (Math.abs(dx) > Math.abs(dy)) {
                newX += dx > 0 ? 1 : -1;
            } else {
                newY += dy > 0 ? 1 : -1;
            }
            if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight && !walls[newX][newY]) {
                enemy.x = newX;
                enemy.y = newY;
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Treasure getTreasure() {
        return treasure;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Trap> getTraps() {
        return traps;
    }

    public boolean[][] getWalls() {
        return walls;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getCellSize() {
        return cellSize;
    }
}