package com.example.tryout;

import com.example.tryout.MyConnection;
import com.example.tryout.DashBoardScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScene {
    private Scene scene;

    public LoginScene(Stage primaryStage) {
        // Creează un AnchorPane pentru a plasa imaginea și formularul
        AnchorPane root = new AnchorPane();

        // Adăugăm clasa CSS pentru a aplica stilul de fundal
        root.getStyleClass().add("root");

        // Crearea unui ImageView pentru imaginea decorativă
        ImageView imageView = new ImageView(new Image(getClass().getResource("/policlinica.png").toExternalForm()));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        // Poziționăm imaginea în colțul din stânga sus
        AnchorPane.setLeftAnchor(imageView, 20.0);
        AnchorPane.setTopAnchor(imageView, 20.0);

        // Adăugăm imaginea la root (AnchorPane)
        root.getChildren().add(imageView);

        // Creează un VBox pentru formularul de login
        VBox vbox = new VBox(10); // Spațiu între elemente
        vbox.setAlignment(Pos.CENTER); // Centrăm formularul

        // Câmpurile de input și butonul
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("CNP:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign Up");
        Label messageLabel = new Label();
        //logica butonului Sign Up
        signUpButton.setOnAction(e -> {
            SignUpScene signUpScene = new SignUpScene(primaryStage);
            primaryStage.setScene(signUpScene.getScene());
        });

        // Logica butonului de login
        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim(); // Elimină spațiile inutile
            String cnp = passwordField.getText().trim();

            System.out.println("Email introdus: " + email);
            System.out.println("CNP introdus: " + cnp);

            authenticateUser(email, cnp, primaryStage);  // Apelează metoda de autentificare
        });


        // Adăugăm câmpurile și butonul în VBox
        vbox.getChildren().addAll(emailLabel, emailField, passwordLabel, passwordField, loginButton, signUpButton, messageLabel);

        // Poziționăm VBox-ul în mijlocul ecranului
        AnchorPane.setTopAnchor(vbox, 270.0);
        AnchorPane.setLeftAnchor(vbox, 750.0);

        // Adăugăm VBox-ul la root (AnchorPane)
        root.getChildren().add(vbox);

        // Adăugăm imaginea "4oameni.png" în colțul din stânga jos
        ImageView bottomImageView = new ImageView(new Image(getClass().getResource("/4oameni.png").toExternalForm()));
        bottomImageView.setFitWidth(500);
        bottomImageView.setFitHeight(470);

        // Poziționăm imaginea în colțul din stânga jos
        AnchorPane.setBottomAnchor(bottomImageView, 20.0);
        AnchorPane.setLeftAnchor(bottomImageView, 0.0);

        // Adăugăm imaginea la root (AnchorPane)
        root.getChildren().add(bottomImageView);

        // Setează dimensiunea scenei pe tot ecranul
        this.scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Setează fereastra să ocupe întregul ecran
        primaryStage.setScene(this.scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public Scene getScene() {
        return this.scene;
    }

    /**
     * Metodă pentru autentificarea utilizatorului folosind baza de date
     */
    private void authenticateUser(String email, String cnp, Stage primaryStage) {
        String query = "SELECT 'client' AS tip, Email, CNP, NULL AS Pozitie " +
                "FROM Clienti WHERE CNP = ? " +
                "UNION " +
                "SELECT 'utilizator' AS tip, Email, CNP, Pozitie " +
                "FROM Utilizator WHERE CNP = ?";

        try (Connection connection = MyConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            System.out.println("connection created");
            System.out.println(connection == null || !isConnectionValid(connection));

            preparedStatement.setString(1, cnp);  // Pentru Clienti
            preparedStatement.setString(2, cnp);  // Pentru Utilizator

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Prepare logged in user session
                Session.setLoggedUserCNP(cnp);

                String tip = resultSet.getString("tip");
                String pozitie = resultSet.getString("Pozitie");

                if ("client".equals(tip)) {
                    System.out.println("Client autentificat.");
                    // Pass client CNP and connection to ClientDashboardScene
                    primaryStage.setScene(new ClientDashboardScene(primaryStage, connection).getScene());
                } else if ("utilizator".equals(tip)) {
                    if (pozitie != null) {
                        switch (pozitie.toLowerCase()) {
                            case "medic":
                                primaryStage.setScene(new MedicDashboardScene(primaryStage,connection).getScene());
                                break;
                            case "asistent medical":
                                primaryStage.setScene(new AsistentMedicalDashboardScene(primaryStage,connection).getScene());
                                break;
                            case "expert contabil":
                                primaryStage.setScene(new ExpertContabilDashboardScene(primaryStage,connection).getScene());
                                break;
                            case "inspector resurse umane":
                                primaryStage.setScene(new InspectorResurseUmaneDashboardScene(primaryStage,connection).getScene());
                                break;
                            case "receptioner":
                                primaryStage.setScene(new ReceptionerDashboardScene(primaryStage, connection).getScene());
                                break;
                            default:
                                System.out.println("Poziție necunoscută pentru utilizator.");
                                break;
                        }
                    } else {
                        System.out.println("Poziție necunoscută.");
                    }
                }
            } else {
                System.out.println("CNP necunoscut.");
            }

        } catch (Exception e) {
            System.err.println("Eroare la autentificare: " + e.getMessage());
        }
    }

    private boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }


}