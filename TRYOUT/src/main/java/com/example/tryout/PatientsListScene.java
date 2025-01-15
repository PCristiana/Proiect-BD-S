package com.example.tryout;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
        root.setStyle("-fx-background-color: #F0F8FF;");

        // Titlul
        Label titleLabel = new Label("Lista pacienților");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2F4F4F;");

        // Tabelul pentru pacienți
        TableView<Patient> patientsTable = new TableView<>();
        configureTableColumns(patientsTable);

        // Mesajul pentru lipsa datelor
        Label emptyMessage = new Label("Nu există pacienți în baza de date.");
        emptyMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080;");
        emptyMessage.setVisible(false);

        // Încărcarea datelor în tabel
        loadPatientsData(connection, patientsTable, emptyMessage);

        // Butonul Înapoi
        Button backButton = new Button("Înapoi");
        styleButton(backButton);
        backButton.setOnAction(e -> {
            ReceptionerDashboardScene receptionerDashboardScene = new ReceptionerDashboardScene(primaryStage, connection);
            primaryStage.setScene(receptionerDashboardScene.getScene());
        });

        // Layout pentru buton
        HBox buttonContainer = new HBox(backButton);
        buttonContainer.setPadding(new Insets(10, 0, 0, 0));
        buttonContainer.setStyle("-fx-alignment: center;");

        // Adaugă toate componentele în layout-ul principal
        root.getChildren().addAll(titleLabel, patientsTable, emptyMessage, buttonContainer);

        this.scene = new Scene(root, 1000, 600); // Dimensiuni mai mari pentru a încăpea istoricul medical
    }

    public Scene getScene() {
        return this.scene;
    }

    private void configureTableColumns(TableView<Patient> tableView) {
        // Coloana pentru nume complet
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Nume complet");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameColumn.setMinWidth(300);

        // Coloana pentru CNP
        TableColumn<Patient, String> cnpColumn = new TableColumn<>("CNP");
        cnpColumn.setCellValueFactory(cellData -> cellData.getValue().cnpProperty());
        cnpColumn.setMinWidth(200);

        // Coloana pentru istoricul medical
        TableColumn<Patient, String> historyColumn = new TableColumn<>("Istoric medical");
        historyColumn.setCellValueFactory(cellData -> cellData.getValue().medicalHistoryProperty());
        historyColumn.setMinWidth(500);

        tableView.getColumns().addAll(nameColumn, cnpColumn, historyColumn);
    }

    private void loadPatientsData(Connection connection, TableView<Patient> tableView, Label emptyMessage) {
        tableView.getItems().clear();

        if (connection == null || !isConnectionValid(connection)) {
            System.err.println("Conexiunea este nulă sau închisă. Se încearcă reconectarea...");
            connection = MyConnection.getConnection();
            if (connection == null) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        // Interogare SQL pentru a include istoricul medical
        String query = """
            SELECT c.nume_client, c.prenume_client, c.CNP,
                   GROUP_CONCAT(CONCAT(p.data_programare, ' ', p.ora_programare, ' - ', s.nume_serviciu) SEPARATOR '; ') AS istoric_medical
            FROM Clienti c
            LEFT JOIN Programari p ON c.id_client = p.id_client
            LEFT JOIN serviciimedicale s ON p.id_serviciu = s.id_serviciu
            GROUP BY c.id_client
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String nume = rs.getString("nume_client");
                String prenume = rs.getString("prenume_client");
                String cnp = rs.getString("CNP");
                String istoricMedical = rs.getString("istoric_medical");

                if (istoricMedical == null || istoricMedical.isEmpty()) {
                    istoricMedical = "Nu există programări anterioare.";
                }

                tableView.getItems().add(new Patient(nume + " " + prenume, cnp, istoricMedical));
            }

            emptyMessage.setVisible(!hasData); // Afișează mesajul dacă nu există date
        } catch (SQLException e) {
            System.err.println("Eroare la executarea interogării: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20;");
    }

    private boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
