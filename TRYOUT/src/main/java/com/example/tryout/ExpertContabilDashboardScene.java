
// Fereastra pentru Expert Contabil
package com.example.tryout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ExpertContabilDashboardScene {
    private Scene scene;

    public ExpertContabilDashboardScene(Stage primaryStage) {
        VBox root = new VBox(10);
        root.getChildren().add(new Label("Welcome to the Expert Contabil Dashboard!"));

        this.scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return this.scene;
    }
}