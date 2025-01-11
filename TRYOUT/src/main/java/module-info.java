module com.example.tryout {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive mysql.connector.java;


    opens com.example.tryout to javafx.fxml;
    exports com.example.tryout;
}