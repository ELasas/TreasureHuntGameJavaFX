package com.example.treasurehuntgame.game;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.database.DatabaseManager;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class GameEngine {
    private final GameDifficulty difficulty;
    private final String playerName;
    private final TreasureHuntGame mainApp;
    private final DatabaseManager dbManager;

    private Stage gameStage;
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private final int GRID_SIZE = 25;
    private final int CELL_SIZE = 20;
    private int gridWidth, gridHeight;
    private boolean[][] walls;
    private boolean gameRunning = true;
    private boolean gameWon = false;
    private long gameStartTime;
    private int playerLives;
    private long timeLimit;

    private Player player;
    private Treasure treasure;
    private List<Trap> traps;
    private List<Enemy> enemies;
    private boolean randomMovement = false;

    public GameEngine(GameDifficulty difficulty, String playerName, TreasureHuntGame mainApp) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.mainApp = mainApp;
        this.dbManager = new DatabaseManager();

        switch (difficulty) {
            case EASY:
                gridWidth = gridHeight = 20;
                playerLives = 5;
                timeLimit = 0;
                break;
            case MEDIUM:
                gridWidth = gridHeight = 25;
                playerLives = 3;
                timeLimit = 0;
                break;
            case HARD:
                gridWidth = gridHeight = 30;
                playerLives = 2;
                timeLimit = 120000;
                break;
        }
    }

    public void startGame() {
        initializeGame();
        createGameStage();
        gameStartTime = System.currentTimeMillis();
        startGameLoop();
    }

    private void initializeGame() {
        walls = new boolean[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                walls[x][y] = true;
            }
        }

        Random rand = new Random();
        Stack<int[]> stack = new Stack<>();
        walls[1][1] = false;
        stack.push(new int[]{1, 1});

        int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];
            List<int[]> neighbors = new ArrayList<>();

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                if (nx > 0 && nx < gridWidth - 1 && ny > 0 && ny < gridHeight - 1 && walls[nx][ny]) {
                    neighbors.add(new int[]{nx, ny});
                }
            }

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                int[] next = neighbors.get(rand.nextInt(neighbors.size()));
                int nx = next[0];
                int ny = next[1];
                walls[nx][ny] = false;
                walls[(x + nx) / 2][(y + ny) / 2] = false;
                stack.push(new int[]{nx, ny});
            }
        }

        walls[gridWidth - 2][gridHeight - 2] = false;

        player = new Player(1, 1);
        walls[1][1] = false;

        treasure = new Treasure(gridWidth - 2, gridHeight - 2);
        walls[gridWidth - 2][gridHeight - 2] = false;

        traps = new ArrayList<>();
        int trapCount = difficulty == GameDifficulty.EASY ? 5 :
                difficulty == GameDifficulty.MEDIUM ? 12 : 20;

        Random random = new Random();
        int attempts = 0;
        int maxAttempts = trapCount * 10;

        for (int i = 0; i < trapCount && attempts < maxAttempts; attempts++) {
            int x = random.nextInt(gridWidth - 2) + 1;
            int y = random.nextInt(gridHeight - 2) + 1;

            if (!walls[x][y] &&
                    !(x == player.x && y == player.y) &&
                    !(x == treasure.x && y == treasure.y) &&
                    traps.stream().noneMatch(t -> t.x == x && t.y == y)) {
                traps.add(new Trap(x, y));
                i++;
            }
        }

        enemies = new ArrayList<>();
        if (difficulty != GameDifficulty.EASY) {
            int enemyCount = difficulty == GameDifficulty.MEDIUM ? 3 : 6;
            attempts = 0;
            maxAttempts = enemyCount * 10;

            for (int i = 0; i < enemyCount && attempts < maxAttempts; attempts++) {
                int x = random.nextInt(gridWidth - 2) + 1;
                int y = random.nextInt(gridHeight - 2) + 1;

                if (!walls[x][y] &&
                        !(x == player.x && y == player.y) &&
                        !(x == treasure.x && y == treasure.y) &&
                        traps.stream().noneMatch(t -> t.x == x && t.y == y) &&
                        enemies.stream().noneMatch(e -> e.x == x && e.y == y)) {
                    enemies.add(new Enemy(x, y));
                    i++;
                }
            }
        }
    }

    private void createGameStage() {
        gameStage = new Stage();
        gameStage.setTitle("Treasure Hunt - " + difficulty + " Level");
        gameStage.setResizable(true); // يمكن تكبير أو تصغير النافذة
        gameStage.setMinWidth(600);
        gameStage.setMinHeight(500);

        VBox gameLayout = new VBox(10);
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.setPadding(new Insets(10));
        gameLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #4a2c2a, #8d5524);"); // تغيير لون الخلفية
        gameLayout.getStyleClass().add("vbox");

        HBox infoPanel = new HBox(20);
        infoPanel.setAlignment(Pos.CENTER);
        infoPanel.setPadding(new Insets(10));
        infoPanel.getStyleClass().add("info-panel");

        Label livesLabel = new Label("Lives: " + playerLives);
        livesLabel.setTextFill(Color.WHITE);
        livesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        livesLabel.getStyleClass().add("label");

        Label difficultyLabel = new Label("Difficulty: " + difficulty);
        difficultyLabel.setTextFill(Color.GOLD);
        difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        difficultyLabel.getStyleClass().add("label");

        Label timeLabel = new Label();
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        timeLabel.getStyleClass().add("label");

        if (timeLimit > 0) {
            timeLabel.setText("Time: " + (timeLimit / 1000) + "s");
        } else {
            timeLabel.setText("Time: ∞");
        }

        infoPanel.getChildren().addAll(livesLabel, difficultyLabel, timeLabel);

        canvas = new Canvas(gridWidth * CELL_SIZE, gridHeight * CELL_SIZE);
        gc = canvas.getGraphicsContext2D();

        Label controlsLabel = new Label("Use ARROW KEYS/WASD to move | Press 'R' for Random Movement | Find the 💰 treasure!");
        controlsLabel.setTextFill(Color.WHITE);
        controlsLabel.setFont(Font.font("Arial", 14));
        controlsLabel.getStyleClass().add("label");

        Button menuButton = new Button("🏠 BACK TO MENU");
        menuButton.getStyleClass().addAll("button", "menu-back-button");
        menuButton.setOnAction(e -> {
            endGame(false);
            gameStage.close();
            mainApp.returnToMainMenu();
        });

        gameLayout.getChildren().addAll(infoPanel, canvas, controlsLabel, menuButton);

        Scene gameScene = new Scene(gameLayout);
        gameScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        gameScene.setOnKeyPressed(e -> {
            if (!gameRunning) return;

            KeyCode key = e.getCode();
            int newX = player.x;
            int newY = player.y;

            if (key == KeyCode.R) {
                randomMovement = !randomMovement;
                controlsLabel.setText("Random Movement: " + (randomMovement ? "ON" : "OFF") + " | Use ARROW KEYS/WASD to move manually | Find the 💰 treasure!");
                return;
            }

            if (!randomMovement) {
                switch (key) {
                    case UP: case W: newY--; break;
                    case DOWN: case S: newY++; break;
                    case LEFT: case A: newX--; break;
                    case RIGHT: case D: newX++; break;
                    default: return;
                }
                movePlayer(newX, newY);
            } else {
                Random rand = new Random();
                int[] directions = {-1, 0, 1};
                int dx = directions[rand.nextInt(3)];
                int dy = directions[rand.nextInt(3)];
                newX = player.x + dx;
                newY = player.y + dy;
                movePlayer(newX, newY);
            }
            updateInfoPanel(infoPanel);
        });

        gameStage.setScene(gameScene);
        gameStage.show();
        gameScene.getRoot().requestFocus();
    }

    private void movePlayer(int newX, int newY) {
        if (newX < 0 || newX >= gridWidth || newY < 0 || newY >= gridHeight || walls[newX][newY]) {
            return;
        }

        player.x = newX;
        player.y = newY;

        if (player.x == treasure.x && player.y == treasure.y) {
            gameWon = true;
            endGame(true);
            return;
        }

        for (Trap trap : traps) {
            if (player.x == trap.x && player.y == trap.y && !trap.triggered) {
                trap.triggered = true;
                playerLives--;
                if (playerLives <= 0) {
                    endGame(false);
                    return;
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (Math.abs(player.x - enemy.x) <= 1 && Math.abs(player.y - enemy.y) <= 1) {
                playerLives--;
                if (playerLives <= 0) {
                    endGame(false);
                    return;
                }
            }
        }
    }

    private void startGameLoop() {
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
                    moveEnemies();
                    lastEnemyMove = now;
                }

                render();
            }
        };
        gameLoop.start();
    }

    private void moveEnemies() {
        Random random = new Random();
        for (Enemy enemy : enemies) {
            int moveType = random.nextInt(3);
            int newX = enemy.x;
            int newY = enemy.y;

            if (moveType == 0) {
                if (player.x > enemy.x) newX++;
                else if (player.x < enemy.x) newX--;
                else if (player.y > enemy.y) newY++;
                else if (player.y < enemy.y) newY--;
            } else {
                int direction = random.nextInt(4);
                switch (direction) {
                    case 0: newY--; break;
                    case 1: newY++; break;
                    case 2: newX--; break;
                    case 3: newX++; break;
                }
            }

            int finalNewX = newX;
            int finalNewY = newY;
            if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight &&
                    !walls[newX][newY] &&
                    enemies.stream().noneMatch(e -> e != enemy && e.x == finalNewX && e.y == finalNewY)) {
                enemy.x = newX;
                enemy.y = newY;
            }
        }
    }

    private void render() {
        gc.setFill(Color.SANDYBROWN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BROWN);
        gc.setLineWidth(1);
        for (int x = 0; x <= gridWidth; x++) {
            gc.strokeLine(x * CELL_SIZE, 0, x * CELL_SIZE, gridHeight * CELL_SIZE);
        }
        for (int y = 0; y <= gridHeight; y++) {
            gc.strokeLine(0, y * CELL_SIZE, gridWidth * CELL_SIZE, y * CELL_SIZE);
        }

        gc.setFill(Color.DARKGRAY);
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (walls[x][y]) {
                    gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        gc.setFill(Color.GOLD);
        gc.fillOval(treasure.x * CELL_SIZE + 2, treasure.y * CELL_SIZE + 2, CELL_SIZE - 4, CELL_SIZE - 4);

        gc.setFill(Color.RED);
        for (Trap trap : traps) {
            if (!trap.triggered) {
                gc.fillRect(trap.x * CELL_SIZE + 4, trap.y * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
            }
        }

        gc.setFill(Color.PURPLE);
        for (Enemy enemy : enemies) {
            gc.fillOval(enemy.x * CELL_SIZE + 1, enemy.y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
        }

        gc.setFill(Color.BLUE);
        gc.fillOval(player.x * CELL_SIZE + 1, player.y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
    }

    private void updateInfoPanel(HBox infoPanel) {
        Label livesLabel = (Label) infoPanel.getChildren().get(0);
        livesLabel.setText("Lives: " + playerLives);

        if (timeLimit > 0) {
            Label timeLabel = (Label) infoPanel.getChildren().get(2);
            long remainingTime = Math.max(0, timeLimit - (System.currentTimeMillis() - gameStartTime));
            timeLabel.setText("Time: " + (remainingTime / 1000) + "s");
        }
    }

    private void endGame(boolean won) {
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
            gameStage.close();
            if (result.get() == playAgainButton) {
                mainApp.startGame(difficulty);
            } else {
                mainApp.returnToMainMenu();
            }
        }
    }
}