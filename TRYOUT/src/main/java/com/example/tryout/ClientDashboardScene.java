package com.example.tryout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientDashboardScene {
    private Scene scene;

    public ClientDashboardScene(Stage primaryStage, Connection connection, String clientCNP) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;"); // Background albastru

        // Title
        Label titleLabel = new Label("PACIENT");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GRAY);

        // Personal Details Section
        Label personalDetailsLabel = new Label("Personal Details:");
        personalDetailsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label personalDetails = new Label();
        personalDetails.setFont(Font.font("Arial", 14));
        personalDetails.setWrapText(true);

        // Current Appointment Section
        Label currentAppointmentLabel = new Label("Current Appointment:");
        currentAppointmentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label currentAppointment = new Label();
        currentAppointment.setFont(Font.font("Arial", 14));
        currentAppointment.setWrapText(true);

        // Appointment History Section
        Label historyLabel = new Label("Appointment History:");
        historyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        TableView<Appointment> historyTable = new TableView<>();

        // Columns for TableView
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Appointment Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("data_programare"));

        TableColumn<Appointment, String> timeColumn = new TableColumn<>("Appointment Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("ora_programare"));

        TableColumn<Appointment, String> serviceColumn = new TableColumn<>("Service");
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("serviciu"));

        TableColumn<Appointment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        historyTable.getColumns().addAll(dateColumn, timeColumn, serviceColumn, statusColumn);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load Data
        loadPersonalDetails(connection, clientCNP, personalDetails);
        loadCurrentAppointment(connection, clientCNP, currentAppointment);
        loadAppointmentHistory(connection, clientCNP, historyTable);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4682B4; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> {
            loadPersonalDetails(connection, clientCNP, personalDetails);
            loadCurrentAppointment(connection, clientCNP, currentAppointment);
            historyTable.getItems().clear();
            loadAppointmentHistory(connection, clientCNP, historyTable);
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #FF6347; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });

        buttonBox.getChildren().addAll(refreshButton, logoutButton);

        // Add elements to root
        root.getChildren().addAll(
                titleLabel,
                personalDetailsLabel, personalDetails,
                currentAppointmentLabel, currentAppointment,
                historyLabel, historyTable,
                buttonBox
        );

        this.scene = new Scene(root, 900, 700);
    }

    public Scene getScene() {
        return this.scene;
    }

    private void loadPersonalDetails(Connection connection, String clientCNP, Label personalDetails) {
        String query = "SELECT nume, prenume, contact, email, adresa FROM Clienti WHERE CNP = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String details = String.format("Nume: %s %s\nEmail: %s\nAdresa: %s\nContact: %s",
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getString("email"),
                        rs.getString("adresa"),
                        rs.getString("contact"));
                personalDetails.setText(details);
            } else {
                personalDetails.setText("Nu sunt detalii personale găsite.");
            }
        } catch (Exception e) {
            personalDetails.setText("Eroare la găsirea informațiilor personale.");
            e.printStackTrace();
        }
    }

    private void loadCurrentAppointment(Connection connection, String clientCNP, Label currentAppointment) {
        String query = "SELECT p.data_programare, p.ora_programare, p.status, s.nume_serviciu AS serviciu " +
                "FROM Programari p " +
                "JOIN ServiciiMedicale s ON p.id_serviciu = s.id_serviciu " +
                "WHERE p.id_client = (SELECT id_client FROM Clienti WHERE CNP = ?) " +
                "AND p.data_programare >= CURRENT_DATE " +
                "ORDER BY p.data_programare LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String appointment = String.format("Data: %s\nTimp: %s\nServiciu: %s\nStatus: %s",
                        rs.getString("data_programare"),
                        rs.getString("ora_programare"),
                        rs.getString("serviciu"),
                        rs.getString("status"));
                currentAppointment.setText(appointment);
            } else {
                currentAppointment.setText("No current appointment.");
            }
        } catch (Exception e) {
            currentAppointment.setText("Error loading current appointment.");
            e.printStackTrace();
        }
    }

    private void loadAppointmentHistory(Connection connection, String clientCNP, TableView<Appointment> historyTable) {
        String query = "SELECT p.data_programare, p.ora_programare, p.status, s.nume_serviciu AS serviciu " +
                "FROM Programari p " +
                "JOIN ServiciiMedicale s ON p.id_serviciu = s.id_serviciu " +
                "WHERE p.id_client = (SELECT id_client FROM Clienti WHERE CNP = ?) " +
                "ORDER BY p.data_programare DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historyTable.getItems().add(new Appointment(
                        rs.getString("data_programare"),
                        rs.getString("ora_programare"),
                        rs.getString("serviciu"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
