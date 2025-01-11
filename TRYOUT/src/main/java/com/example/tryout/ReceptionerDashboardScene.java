package com.example.tryout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReceptionerDashboardScene {
    private Scene scene;

    public ReceptionerDashboardScene(Stage primaryStage, Connection connection) {
        if (connection == null || !isConnectionValid(connection)) {
            System.err.println("Conexiunea inițială este invalidă. Se încearcă reconectarea...");
            connection = MyConnection.getConnection();
            if (connection == null) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;"); // Background albastru

        // Title
        Label titleLabel = new Label("Dashboard Receptioner");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GRAY);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Buton pentru a vizualiza toți pacienții
        Button viewPatientsButton = new Button("Vezi toți pacienții");
        viewPatientsButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        Connection finalConnection = connection;
        viewPatientsButton.setOnAction(e -> {
            PatientsListScene patientsListScene = new PatientsListScene(primaryStage, finalConnection);
            primaryStage.setScene(patientsListScene.getScene());
        });

        // Buton pentru a adăuga o programare
        Button addAppointmentButton = new Button("Adaugă programare");
        addAppointmentButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        addAppointmentButton.setOnAction(e -> {
            openAddAppointmentForm(primaryStage, connection);
        });

        // Buton pentru logout
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });

        buttonBox.getChildren().addAll(viewPatientsButton, addAppointmentButton, logoutButton);

        // Add elements to root
        root.getChildren().addAll(titleLabel, buttonBox);

        this.scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return this.scene;
    }

    private void openAddAppointmentForm(Stage primaryStage, Connection connection) {
        VBox formRoot = new VBox(15);
        formRoot.setPadding(new Insets(20));
        formRoot.setStyle("-fx-background-color: #F0F8FF;");
        formRoot.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Adauga programare");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Câmpuri pentru detaliile programării
        TextField clientCnpField = new TextField();
        clientCnpField.setPromptText("CNP pacient");

        DatePicker appointmentDatePicker = new DatePicker();
        appointmentDatePicker.setPromptText("Alege data");

        TextField appointmentTimeField = new TextField();
        appointmentTimeField.setPromptText("Ora programării");

        ComboBox<String> serviceComboBox = new ComboBox<>();
        serviceComboBox.getItems().addAll("Consultație", "Analize", "Tratament");
        serviceComboBox.setPromptText("Alege serviciu");

        Button saveButton = new Button("Salvează programarea");
        saveButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            String clientCNP = clientCnpField.getText();
            String service = serviceComboBox.getValue();
            String appointmentDate = appointmentDatePicker.getValue().toString();
            String appointmentTime = appointmentTimeField.getText();
            addAppointment(connection, clientCNP, service, appointmentDate, appointmentTime);
            primaryStage.setScene(this.scene); // Revenire la dashboard
        });

        Button cancelButton = new Button("Anulează");
        cancelButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> primaryStage.setScene(this.scene));

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        formRoot.getChildren().addAll(formTitle, clientCnpField, appointmentDatePicker, appointmentTimeField, serviceComboBox, buttonBox);

        Scene formScene = new Scene(formRoot, 600, 400);
        primaryStage.setScene(formScene);
    }

    private void addAppointment(Connection connection, String clientCNP, String service, String date, String time) {
        if (connection == null || !isConnectionValid(connection)) {
            System.err.println("Conexiunea la baza de date este închisă. Se încearcă reconectarea...");
            connection = MyConnection.getConnection(); // Obține o conexiune nouă
            if (connection == null) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "INSERT INTO Programari (id_client, id_serviciu, data_programare, ora_programare, status) " +
                "VALUES ((SELECT id_client FROM Clienti WHERE CNP = ?), (SELECT id_serviciu FROM ServiciiMedicale WHERE nume_serviciu = ?), ?, ?, 'Programat')";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            stmt.setString(2, service);
            stmt.setString(3, date);
            stmt.setString(4, time);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Programare adăugată cu succes.");
            } else {
                System.out.println("Eroare la adăugarea programării.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la adăugarea programării: " + e.getMessage());
            e.printStackTrace();
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
