package com.example.treasurehuntgame.scenes;

import com.example.treasurehuntgame.TreasureHuntGame;
import com.example.treasurehuntgame.database.DatabaseManager;
import com.example.treasurehuntgame.database.GameRecord;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Objects;

public class GameHistoryScene {
    private final TreasureHuntGame app;
    private final DatabaseManager dbManager;
    private final Scene scene;
    private String currentPlayer;

    public GameHistoryScene(TreasureHuntGame app, DatabaseManager dbManager) {
        this.app = app;
        this.dbManager = dbManager;
        this.scene = createGameHistoryScene();
    }

    private Scene createGameHistoryScene() {
        VBox historyLayout = new VBox(20);
        historyLayout.setAlignment(Pos.TOP_CENTER);
        historyLayout.setPadding(new Insets(30));
        historyLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1c2526, #2f4f4f);");
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
        backButton.setOnAction(e -> app.showMainMenu());

        historyLayout.getChildren().addAll(titleLabel, scrollPane, backButton);

        Scene historyScene = new Scene(historyLayout, 700, 500);
        historyScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/treasurehuntgame/styles.css")).toExternalForm());
        return historyScene;
    }

    public void setCurrentPlayer(String player) {
        this.currentPlayer = player;
    }

    public Scene getScene() {
        return scene;
    }
}
