package com.example.treasurehuntgame.game.ui;

import com.example.treasurehuntgame.game.core.GameDifficulty;
import com.example.treasurehuntgame.game.entities.Enemy;
import com.example.treasurehuntgame.game.entities.Player;
import com.example.treasurehuntgame.game.entities.Trap;
import com.example.treasurehuntgame.game.entities.Treasure;

import java.util.*;

public class GameBoard {
    private final int CELL_SIZE = 20;
    private int gridWidth, gridHeight;
    private boolean[][] walls;
    private Player player;
    private Treasure treasure;
    private List<Trap> traps;
    private List<Enemy> enemies;
    private final GameDifficulty difficulty;

    public GameBoard(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        switch (difficulty) {
            case EASY:
                gridWidth = gridHeight = 20;
                break;
            case MEDIUM:
                gridWidth = gridHeight = 25;
                break;
            case HARD:
                gridWidth = gridHeight = 30;
                break;
        }
    }

    public void initializeGame() {
        walls = new boolean[gridWidth][gridHeight];


        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                walls[x][y] = true;
            }
        }


        generateMazeWithGuaranteedPath();


        player = new Player(1, 1);
        treasure = new Treasure(gridWidth - 2, gridHeight - 2);


        ensurePathExists();


        placeTrapsAndEnemies();
    }

    private void generateMazeWithGuaranteedPath() {
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


        addExtraPaths(rand);
    }

    private void addExtraPaths(Random rand) {
        int extraPaths = difficulty == GameDifficulty.EASY ? 3 :
                difficulty == GameDifficulty.MEDIUM ? 5 : 8;

        for (int i = 0; i < extraPaths; i++) {
            int x = rand.nextInt(gridWidth - 2) + 1;
            int y = rand.nextInt(gridHeight - 2) + 1;

            if (walls[x][y]) {
                walls[x][y] = false;

                if (rand.nextBoolean()) {
                    int direction = rand.nextInt(4);
                    int[] dx = {0, 0, -1, 1};
                    int[] dy = {-1, 1, 0, 0};

                    int nx = x + dx[direction];
                    int ny = y + dy[direction];

                    if (nx > 0 && nx < gridWidth - 1 && ny > 0 && ny < gridHeight - 1) {
                        walls[nx][ny] = false;
                    }
                }
            }
        }
    }

    private void ensurePathExists() {
        if (!hasPath(player.x, player.y, treasure.x, treasure.y)) {
            createGuaranteedPath();
        }
    }

    private boolean hasPath(int startX, int startY, int endX, int endY) {
        boolean[][] visited = new boolean[gridWidth][gridHeight];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY});
        visited[startX][startY] = true;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (x == endX && y == endY) {
                return true;
            }

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight &&
                        !walls[nx][ny] && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
        return false;
    }

    private void createGuaranteedPath() {

        List<int[]> path = findShortestPath(player.x, player.y, treasure.x, treasure.y);


        if (path.isEmpty()) {
            createDirectPath();
        } else {

            for (int[] point : path) {
                walls[point[0]][point[1]] = false;
            }
        }
    }

    private List<int[]> findShortestPath(int startX, int startY, int endX, int endY) {
        boolean[][] visited = new boolean[gridWidth][gridHeight];
        int[][] parent = new int[gridWidth][gridHeight];
        Queue<int[]> queue = new LinkedList<>();

        // Initialize parent array
        for (int i = 0; i < gridWidth; i++) {
            Arrays.fill(parent[i], -1);
        }

        queue.offer(new int[]{startX, startY});
        visited[startX][startY] = true;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (x == endX && y == endY) {
                return reconstructPath(parent, startX, startY, endX, endY);
            }

            for (int i = 0; i < directions.length; i++) {
                int[] dir = directions[i];
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight &&
                        !visited[nx][ny]) {

                    visited[nx][ny] = true;
                    parent[nx][ny] = i;
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
        return new ArrayList<>();
    }

    private List<int[]> reconstructPath(int[][] parent, int startX, int startY, int endX, int endY) {
        List<int[]> path = new ArrayList<>();
        int x = endX, y = endY;
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!(x == startX && y == startY)) {
            path.add(new int[]{x, y});
            int dir = parent[x][y];
            if (dir == -1) break;
            int[] oppositeDir = directions[dir ^ 1];
            x += oppositeDir[0];
            y += oppositeDir[1];
        }

        Collections.reverse(path);
        return path;
    }

    private void createDirectPath() {
        int currentX = player.x;
        int currentY = player.y;
        while (currentX != treasure.x) {
            walls[currentX][currentY] = false;
            currentX += (treasure.x > currentX) ? 1 : -1;
        }
        while (currentY != treasure.y) {
            walls[currentX][currentY] = false;
            currentY += (treasure.y > currentY) ? 1 : -1;
        }
        walls[treasure.x][treasure.y] = false;
    }

    private void placeTrapsAndEnemies() {
        Random rand = new Random();

        traps = new ArrayList<>();
        int trapCount = difficulty == GameDifficulty.EASY ? 5 :
                difficulty == GameDifficulty.MEDIUM ? 12 : 20;

        for (int i = 0; i < trapCount; ) {
            int x = rand.nextInt(gridWidth - 2) + 1;
            int y = rand.nextInt(gridHeight - 2) + 1;

            if (!walls[x][y] &&
                    !(x == player.x && y == player.y) &&
                    !(x == treasure.x && y == treasure.y) &&
                    traps.stream().noneMatch(t -> t.x == x && t.y == y) &&
                    !isOnCriticalPath(x, y)) {
                traps.add(new Trap(x, y));
                i++;
            }
        }

        enemies = new ArrayList<>();
        if (difficulty != GameDifficulty.EASY) {
            int enemyCount = difficulty == GameDifficulty.MEDIUM ? 3 : 6;
            for (int i = 0; i < enemyCount; ) {
                int x = rand.nextInt(gridWidth - 2) + 1;
                int y = rand.nextInt(gridHeight - 2) + 1;

                if (!walls[x][y] &&
                        !(x == player.x && y == player.y) &&
                        !(x == treasure.x && y == treasure.y) &&
                        traps.stream().noneMatch(t -> t.x == x && t.y == y) &&
                        enemies.stream().noneMatch(e -> e.x == x && e.y == y) &&
                        !isOnCriticalPath(x, y)) {
                    enemies.add(new Enemy(x, y));
                    i++;
                }
            }
        }
    }

    private boolean isOnCriticalPath(int x, int y) {

        if (walls[x][y]) return false;

        walls[x][y] = true;
        boolean pathExists = hasPath(player.x, player.y, treasure.x, treasure.y);
        walls[x][y] = false;

        return !pathExists;
    }

    public void movePlayer(int newX, int newY) {
        if (newX < 0 || newX >= gridWidth || newY < 0 || newY >= gridHeight || walls[newX][newY]) {
            return;
        }
        player.x = newX;
        player.y = newY;
    }

    public void moveEnemies() {
        Random rand = new Random();
        for (Enemy enemy : enemies) {
            int moveType = rand.nextInt(3);
            int newX = enemy.x;
            int newY = enemy.y;

            if (moveType == 0) {
                if (player.x > enemy.x) newX++;
                else if (player.x < enemy.x) newX--;
                else if (player.y > enemy.y) newY++;
                else if (player.y < enemy.y) newY--;
            } else {

                int direction = rand.nextInt(4);
                switch (direction) {
                    case 0: newY--; break; // Up
                    case 1: newY++; break; // Down
                    case 2: newX--; break; // Left
                    case 3: newX++; break; // Right
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

    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getCellSize() { return CELL_SIZE; }
    public boolean[][] getWalls() { return walls; }
    public Player getPlayer() { return player; }
    public Treasure getTreasure() { return treasure; }
    public List<Trap> getTraps() { return traps; }
    public List<Enemy> getEnemies() { return enemies; }
}