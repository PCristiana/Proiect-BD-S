package com.example.tryout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientDashboardScene {
    private Scene scene;

    public ClientDashboardScene(Stage primaryStage) {
        VBox root = new VBox(10);
        root.getChildren().add(new Label("Welcome to the Client Dashboard!"));

        this.scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return this.scene;
    }
}