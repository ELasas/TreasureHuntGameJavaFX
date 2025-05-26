module com.example.treasurehuntgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.treasurehuntgame to javafx.fxml;
    exports com.example.treasurehuntgame;
    exports com.example.treasurehuntgame.scenes;
    opens com.example.treasurehuntgame.scenes to javafx.fxml;
}