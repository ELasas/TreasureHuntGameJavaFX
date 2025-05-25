package com.example.treasurehuntgame;

import com.example.treasurehuntgame.database.DatabaseManager;
import com.example.treasurehuntgame.database.GameRecord;
import com.example.treasurehuntgame.game.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class TreasureHuntGame extends Application {
    private Stage primaryStage;
    private String currentPlayer;
    private DatabaseManager dbManager;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.dbManager = new DatabaseManager();
        dbManager.initializeTables();

        primaryStage.setTitle("Treasure Hunt Game");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);

        showLoginScene();
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

    private void showLoginScene() {
        VBox loginLayout = new VBox(20);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(50));
        loginLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a44, #2c3e50);");

        Label titleLabel = new Label("🏴‍☠️ TREASURE HUNT 🏴‍☠️");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.getStyleClass().add("title");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(350);
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(350);
        passwordField.getStyleClass().add("password-field");

        Button loginButton = new Button("LOGIN");
        loginButton.getStyleClass().addAll("button", "easy-button");

        Button registerButton = new Button("REGISTER");
        registerButton.getStyleClass().addAll("button");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.getStyleClass().add("label");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in all fields!");
                return;
            }

            if (dbManager.authenticateUser(username, password)) {
                currentPlayer = username;
                showMainMenu();
            } else {
                messageLabel.setText("Invalid username or password!");
            }
        });

        registerButton.setOnAction(e -> showRegistrationScene());

        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getStyleClass().add("hbox");
        buttonBox.getChildren().addAll(loginButton, registerButton);

        loginLayout.getChildren().addAll(
                titleLabel,
                new Label("Enter your credentials to start playing:") {{
                    setTextFill(Color.WHITE);
                    setFont(Font.font("Arial", 18));
                    getStyleClass().add("label");
                }},
                usernameField,
                passwordField,
                buttonBox,
                messageLabel
        );

        Scene loginScene = new Scene(loginLayout, 600, 500);
        loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        primaryStage.setScene(loginScene);
    }

    private void showRegistrationScene() {
        VBox regLayout = new VBox(20);
        regLayout.setAlignment(Pos.CENTER);
        regLayout.setPadding(new Insets(50));
        regLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #6c3483, #8e44ad);");

        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.getStyleClass().add("title");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose Username (3-20 characters)");
        usernameField.setMaxWidth(400);
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose Password (min 4 characters)");
        passwordField.setMaxWidth(400);
        passwordField.getStyleClass().add("password-field");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(400);
        confirmPasswordField.getStyleClass().add("password-field");

        Button createButton = new Button("CREATE ACCOUNT");
        createButton.getStyleClass().addAll("button", "easy-button");

        Button backButton = new Button("BACK TO LOGIN");
        backButton.getStyleClass().addAll("button", "logout-button");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.YELLOW);
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.getStyleClass().add("label");

        createButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                messageLabel.setText("Please fill in all fields!");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (username.length() < 3 || username.length() > 20) {
                messageLabel.setText("Username must be 3-20 characters!");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (password.length() < 4) {
                messageLabel.setText("Password must be at least 4 characters!");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match!");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (dbManager.registerUser(username, password)) {
                messageLabel.setText("Account created successfully! Returning to login...");
                messageLabel.setTextFill(Color.LIGHTGREEN);

                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::showLoginScene);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Username already exists! Try another one.");
                messageLabel.setTextFill(Color.RED);
            }
        });

        backButton.setOnAction(e -> showLoginScene());

        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                confirmPasswordField.requestFocus();
            }
        });

        confirmPasswordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                createButton.fire();
            }
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getStyleClass().add("hbox");
        buttonBox.getChildren().addAll(createButton, backButton);

        regLayout.getChildren().addAll(
                titleLabel,
                new Label("Join the treasure hunting adventure!") {{
                    setTextFill(Color.WHITE);
                    setFont(Font.font("Arial", 18));
                    getStyleClass().add("label");
                }},
                usernameField,
                passwordField,
                confirmPasswordField,
                buttonBox,
                messageLabel
        );

        Scene regScene = new Scene(regLayout, 600, 550);
        regScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        primaryStage.setScene(regScene);
    }

    private void showMainMenu() {
        VBox menuLayout = new VBox(25);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(50));
        menuLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a44, #2c3e50);");
        menuLayout.getStyleClass().add("vbox");

        Label welcomeLabel = new Label("Welcome, " + currentPlayer + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.GOLD);
        welcomeLabel.getStyleClass().add("welcome");

        Label titleLabel = new Label("🏴‍☠️ TREASURE HUNT 🏴‍☠️");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.getStyleClass().add("title");

        Button easyButton = new Button("🟢 EASY LEVEL");
        easyButton.getStyleClass().addAll("button", "menu-button", "easy-button");

        Button mediumButton = new Button("🟡 MEDIUM LEVEL");
        mediumButton.getStyleClass().addAll("button", "menu-button", "medium-button");

        Button hardButton = new Button("🔴 HARD LEVEL");
        hardButton.getStyleClass().addAll("button", "menu-button", "hard-button");

        Button historyButton = new Button("📊 VIEW GAME HISTORY");
        historyButton.getStyleClass().addAll("button", "menu-button", "history-button");

        Button logoutButton = new Button("🚪 LOGOUT");
        logoutButton.getStyleClass().addAll("button", "menu-button", "logout-button");

        easyButton.setOnAction(e -> startGame(GameDifficulty.EASY));
        mediumButton.setOnAction(e -> startGame(GameDifficulty.MEDIUM));
        hardButton.setOnAction(e -> startGame(GameDifficulty.HARD));
        historyButton.setOnAction(e -> showGameHistory());
        logoutButton.setOnAction(e -> showLoginScene());

        VBox difficultyBox = new VBox(15);
        difficultyBox.setAlignment(Pos.CENTER);
        difficultyBox.getStyleClass().add("vbox");
        difficultyBox.getChildren().addAll(
                new Label("Choose your difficulty:") {{
                    setTextFill(Color.WHITE);
                    setFont(Font.font("Arial", 20));
                    getStyleClass().add("label");
                }},
                easyButton, mediumButton, hardButton
        );

        menuLayout.getChildren().addAll(
                titleLabel,
                welcomeLabel,
                difficultyBox,
                historyButton,
                logoutButton
        );

        Scene menuScene = new Scene(menuLayout, 600, 600);
        menuScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        primaryStage.setScene(menuScene);
    }

    private void showGameHistory() {
        VBox historyLayout = new VBox(20);
        historyLayout.setAlignment(Pos.TOP_CENTER);
        historyLayout.setPadding(new Insets(30));
        historyLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #1a2a44);");
        historyLayout.getStyleClass().add("vbox");

        Label titleLabel = new Label("📊 GAME HISTORY");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.getStyleClass().add("title");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scroll-pane");
        VBox historyContent = new VBox(10);
        historyContent.setPadding(new Insets(20));
        historyContent.getStyleClass().add("history-content");

        List<GameRecord> history = dbManager.getPlayerHistory(currentPlayer);

        if (history.isEmpty()) {
            Label noHistoryLabel = new Label("No games played yet. Start playing to see your history!");
            noHistoryLabel.setTextFill(Color.WHITE);
            noHistoryLabel.setFont(Font.font("Arial", 16));
            noHistoryLabel.getStyleClass().add("label");
            historyContent.getChildren().add(noHistoryLabel);
        } else {
            for (GameRecord record : history) {
                HBox recordBox = new HBox(20);
                recordBox.setAlignment(Pos.CENTER_LEFT);
                recordBox.setPadding(new Insets(10));
                recordBox.getStyleClass().add("history-box");

                String resultIcon = record.result.equals("WIN") ? "🏆" : "💀";
                String difficultyColor = record.difficulty.equals("EASY") ? "🟢" :
                        record.difficulty.equals("MEDIUM") ? "🟡" : "🔴";

                Label recordLabel = new Label(String.format(
                        "%s %s %s | Time: %ds | %s",
                        resultIcon, difficultyColor, record.difficulty, record.timeSpent, record.playTime
                ));
                recordLabel.setTextFill(Color.WHITE);
                recordLabel.setFont(Font.font("Arial", 14));
                recordLabel.getStyleClass().add("label");

                recordBox.getChildren().add(recordLabel);
                historyContent.getChildren().add(recordBox);
            }
        }

        scrollPane.setContent(historyContent);
        scrollPane.setMaxHeight(350);

        Button backButton = new Button("🏠 BACK TO MENU");
        backButton.getStyleClass().addAll("button", "back-button");
        backButton.setOnAction(e -> showMainMenu());

        historyLayout.getChildren().addAll(titleLabel, scrollPane, backButton);

        Scene historyScene = new Scene(historyLayout, 700, 500);
        historyScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        primaryStage.setScene(historyScene);
    }

    public void startGame(GameDifficulty difficulty) {
        GameEngine gameEngine = new GameEngine(difficulty, currentPlayer, this);
        gameEngine.startGame();
    }

    public void returnToMainMenu() { showMainMenu(); }
}