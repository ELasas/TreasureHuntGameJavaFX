module com.example.treasurehuntgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.treasurehuntgame to javafx.fxml;
    exports com.example.treasurehuntgame;
}