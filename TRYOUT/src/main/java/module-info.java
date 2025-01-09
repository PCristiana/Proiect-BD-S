module com.example.tryout {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.tryout to javafx.fxml;
    exports com.example.tryout;
}