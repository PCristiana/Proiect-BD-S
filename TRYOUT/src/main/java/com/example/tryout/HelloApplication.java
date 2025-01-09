package com.example.tryout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login App");

        LoginScene loginScene = new LoginScene(primaryStage);
        primaryStage.setScene(loginScene.getScene());
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
