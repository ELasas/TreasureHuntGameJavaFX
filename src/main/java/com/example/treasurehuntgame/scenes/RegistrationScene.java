package com.example.treasurehuntgame.scenes;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.database.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

public class RegistrationScene {
    private final TreasureHuntGame app;
    private final DatabaseManager dbManager;
    private final Scene scene;

    public RegistrationScene(TreasureHuntGame app, DatabaseManager dbManager) {
        this.app = app;
        this.dbManager = dbManager;
        this.scene = createRegistrationScene();
    }

    private Scene createRegistrationScene() {
        VBox regLayout = new VBox(25);
        regLayout.setAlignment(Pos.CENTER);
        regLayout.setPadding(new Insets(60));
        regLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #4b2e2a, #7b3f3f);");

        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setStyle("-fx-text-alignment: center;");
        titleLabel.getStyleClass().add("title");

        Label subtitleLabel = new Label("Join the treasure hunting adventure!");
        subtitleLabel.setTextFill(Color.WHITE);
        subtitleLabel.setFont(Font.font("Arial", 20));
        subtitleLabel.setAlignment(Pos.CENTER);
        subtitleLabel.setTextAlignment(TextAlignment.CENTER);
        subtitleLabel.setStyle("-fx-text-alignment: center;");
        subtitleLabel.getStyleClass().add("label");

        VBox fieldsBox = new VBox(20);
        fieldsBox.setAlignment(Pos.CENTER);
        fieldsBox.setPadding(new Insets(30, 0, 30, 0));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose Username (3-20 characters)");
        usernameField.setMaxWidth(450);
        usernameField.setPrefHeight(45);
        usernameField.setStyle("-fx-text-alignment: center; -fx-font-size: 14px;");
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose Password (min 4 characters)");
        passwordField.setMaxWidth(450);
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-text-alignment: center; -fx-font-size: 14px;");
        passwordField.getStyleClass().add("password-field");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(450);
        confirmPasswordField.setPrefHeight(45);
        confirmPasswordField.setStyle("-fx-text-alignment: center; -fx-font-size: 14px;");
        confirmPasswordField.getStyleClass().add("password-field");

        fieldsBox.getChildren().addAll(usernameField, passwordField, confirmPasswordField);

        Button createButton = new Button("CREATE ACCOUNT");
        createButton.setPrefWidth(200);
        createButton.setPrefHeight(50);
        createButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        createButton.getStyleClass().addAll("button", "easy-button");

        Button backButton = new Button("BACK TO LOGIN");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        backButton.getStyleClass().addAll("button", "logout-button");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.YELLOW);
        messageLabel.setFont(Font.font("Arial", 16));
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setStyle("-fx-text-alignment: center;");
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
                        javafx.application.Platform.runLater(() -> app.showLoginScene());
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Username already exists! Try another one.");
                messageLabel.setTextFill(Color.RED);
            }
        });

        backButton.setOnAction(e -> app.showLoginScene());

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

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.getStyleClass().add("hbox");
        buttonBox.getChildren().addAll(createButton, backButton);

        regLayout.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                fieldsBox,
                buttonBox,
                messageLabel
        );

        Scene regScene = new Scene(regLayout, 800, 800);
        regScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        return regScene;
    }

    public Scene getScene() {
        return scene;
    }
}