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
        VBox regLayout = new VBox(20);
        regLayout.setAlignment(Pos.CENTER);
        regLayout.setPadding(new Insets(50));
        regLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #4b2e2a, #7b3f3f);");

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
        return regScene;
    }

    public Scene getScene() {
        return scene;
    }
}
