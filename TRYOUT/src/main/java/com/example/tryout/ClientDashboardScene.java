package com.example.tryout;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientDashboardScene {
    private Scene scene;

    public ClientDashboardScene(Stage primaryStage, Connection connection, String clientCNP) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Title
        Label titleLabel = new Label("Welcome to the Client Dashboard!");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Personal Details
        Label personalDetailsLabel = new Label("Personal Details:");
        Label personalDetails = new Label();

        // Current Appointment
        Label currentAppointmentLabel = new Label("Current Appointment:");
        Label currentAppointment = new Label();

        // Appointment History
        Label historyLabel = new Label("Appointment History:");
        TableView<Appointment> historyTable = new TableView<>();

        // Coloană pentru dată
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Coloană pentru serviciu
        TableColumn<Appointment, String> serviceColumn = new TableColumn<>("Service");
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));

        // Coloană pentru status
        TableColumn<Appointment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        historyTable.getColumns().addAll(dateColumn, serviceColumn, statusColumn);

        // Load Data
        loadPersonalDetails(connection, clientCNP, personalDetails);
        loadCurrentAppointment(connection, clientCNP, currentAppointment);
        loadAppointmentHistory(connection, clientCNP, historyTable);

        // Adăugăm elementele la root
        root.getChildren().addAll(
                titleLabel,
                personalDetailsLabel, personalDetails,
                currentAppointmentLabel, currentAppointment,
                historyLabel, historyTable
        );

        this.scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return this.scene;
    }

    // Metoda pentru încărcarea detaliilor personale
    private void loadPersonalDetails(Connection connection, String clientCNP, Label personalDetails) {
        String query = "SELECT Nume, Prenume, Email, Adresa FROM Utilizator WHERE CNP = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String details = String.format("Name: %s %s\nEmail: %s\nAddress: %s",
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getString("Email"),
                        rs.getString("Adresa"));
                personalDetails.setText(details);
            } else {
                personalDetails.setText("No personal details found.");
            }
        } catch (Exception e) {
            personalDetails.setText("Error loading personal details.");
            e.printStackTrace();
        }
    }

    // Metoda pentru încărcarea programării curente
    private void loadCurrentAppointment(Connection connection, String clientCNP, Label currentAppointment) {
        String query = "SELECT Data, Serviciu, Status FROM Programari WHERE CNP = ? AND Data >= CURRENT_DATE ORDER BY Data LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String appointment = String.format("Date: %s\nService: %s\nStatus: %s",
                        rs.getString("Data"),
                        rs.getString("Serviciu"),
                        rs.getString("Status"));
                currentAppointment.setText(appointment);
            } else {
                currentAppointment.setText("No current appointment.");
            }
        } catch (Exception e) {
            currentAppointment.setText("Error loading current appointment.");
            e.printStackTrace();
        }
    }

    // Metoda pentru încărcarea istoricului programărilor
    private void loadAppointmentHistory(Connection connection, String clientCNP, TableView<Appointment> historyTable) {
        String query = "SELECT Data, Serviciu, Status FROM Programari WHERE CNP = ? ORDER BY Data DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historyTable.getItems().add(new Appointment(
                        rs.getString("Data"),
                        rs.getString("Serviciu"),
                        rs.getString("Status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clasa Appointment pentru istoricul programărilor
    public static class Appointment {
        private String date;
        private String service;
        private String status;

        public Appointment(String date, String service, String status) {
            this.date = date;
            this.service = service;
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public String getService() {
            return service;
        }

        public String getStatus() {
            return status;
        }
    }
}
