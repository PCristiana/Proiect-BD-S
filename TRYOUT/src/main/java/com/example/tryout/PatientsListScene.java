package com.example.tryout;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientsListScene {
    private Scene scene;

    public PatientsListScene(Stage primaryStage, Connection connection) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;");

        Label titleLabel = new Label("Lista pacienților");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<Patient> patientsTable = new TableView<>();

        // Coloana pentru nume
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Nume");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        // Coloana pentru CNP
        TableColumn<Patient, String> cnpColumn = new TableColumn<>("CNP");
        cnpColumn.setCellValueFactory(cellData -> cellData.getValue().cnpProperty());

        patientsTable.getColumns().addAll(nameColumn, cnpColumn);

        // Încărcarea datelor în tabel
        loadPatientsData(connection, patientsTable);

        Button backButton = new Button("Înapoi");
        backButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            ReceptionerDashboardScene receptionerDashboardScene = new ReceptionerDashboardScene(primaryStage, connection);
            primaryStage.setScene(receptionerDashboardScene.getScene());
        });

        root.getChildren().addAll(titleLabel, patientsTable, backButton);

        this.scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return this.scene;
    }

    private void loadPatientsData(Connection connection, TableView<Patient> tableView) {
        if (connection == null || !isConnectionValid(connection)) {
            System.err.println("Conexiunea este nulă sau închisă. Se încearcă reconectarea...");
            connection = MyConnection.getConnection();
            if (connection == null) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "SELECT nume_client, prenume_client, CNP FROM Clienti";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nume = rs.getString("nume_client"); // nume_client este corect
                String prenume = rs.getString("prenume_client"); // prenume_client este corect
                String cnp = rs.getString("CNP"); // CNP este corect

                tableView.getItems().add(new Patient(nume + " " + prenume, cnp)); // Concatenează numele complet
            }
        } catch (SQLException e) {
            System.err.println("Eroare la executarea interogării: " + e.getMessage());
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
