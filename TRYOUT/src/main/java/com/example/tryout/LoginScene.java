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
        Button signUpButton=new Button("Sign Up");
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
        // Interogare care combină rezultatele din ambele tabele folosind UNION
        String query = "SELECT 'client' AS tip, Email, CNP FROM Clienti WHERE Email = ? AND CNP = ? " +
                "UNION " +
                "SELECT 'utilizator' AS tip, Email, CNP, Pozitie FROM Utilizator WHERE Email = ? AND CNP = ?";

        try (Connection connection = MyConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Setează parametrii pentru interogare
            preparedStatement.setString(1, email);  // Pentru Clienti
            preparedStatement.setString(2, cnp);    // Pentru Clienti
            preparedStatement.setString(3, email);  // Pentru Utilizator
            preparedStatement.setString(4, cnp);    // Pentru Utilizator

            // Execută interogarea
            System.out.println("Executing query: " + query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Verifică dacă există un utilizator care se potrivește
            if (resultSet.next()) {
                String tip = resultSet.getString("tip");

                if ("client".equals(tip)) {
                    // Dacă este client, redirecționează către dashboard-ul clientului
                    primaryStage.setScene(new ClientDashboardScene(primaryStage).getScene());
                } else if ("utilizator".equals(tip)) {
                    String pozitie = resultSet.getString("Pozitie");

                    // În funcție de poziția utilizatorului, redirecționează la dashboard-ul corespunzător
                    switch (pozitie.toLowerCase()) {
                        case "medic":
                            primaryStage.setScene(new MedicDashboardScene(primaryStage).getScene());
                            break;
                        case "asistent medical":
                            primaryStage.setScene(new AsistentMedicalDashboardScene(primaryStage).getScene());
                            break;
                        case "expert contabil":
                            primaryStage.setScene(new ExpertContabilDashboardScene(primaryStage).getScene());
                            break;
                        case "inspector resurse umane":
                            primaryStage.setScene(new InspectorResurseUmaneDashboardScene(primaryStage).getScene());
                            break;
                        case "receptioner":
                            primaryStage.setScene(new ReceptionerDashboardScene(primaryStage).getScene());
                            break;
                        default:
                            System.out.println("Poziție necunoscută.");
                            break;
                    }
                }
            } else {
                System.out.println("User not found.");
                // Poți adăuga un mesaj de eroare pe interfața de login aici
            }

        } catch (Exception e) {
            System.err.println("Eroare la autentificare: " + e.getMessage());
        }
    }



}