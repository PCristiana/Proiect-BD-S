package com.example.tryout;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class DashBoardScene {
    private Scene scene;

    public DashBoardScene() {
        VBox layout = new VBox(10);
        layout.getChildren().add(new Label("Welcome to the Dashboard!"));
        this.scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return this.scene;
    }
}
