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

public class LoginScene {
    private final TreasureHuntGame app;
    private final DatabaseManager dbManager;
    private final Scene scene;

    public LoginScene(TreasureHuntGame app, DatabaseManager dbManager) {
        this.app = app;
        this.dbManager = dbManager;
        this.scene = createLoginScene();
    }

    private Scene createLoginScene() {
        VBox loginLayout = new VBox(20);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(50));
        loginLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1c2526, #2f4f4f);");

        Label titleLabel = new Label("🏴‍️ TREASURE HUNT 🏴‍️");
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
                app.setCurrentPlayer(username);
                app.showMainMenu();
            } else {
                messageLabel.setText("Invalid username or password!");
            }
        });

        registerButton.setOnAction(e -> app.showRegistrationScene());

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

        Scene loginScene = new Scene(loginLayout, 800, 800);
        loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        return loginScene;
    }

    public Scene getScene() {
        return scene;
    }
}