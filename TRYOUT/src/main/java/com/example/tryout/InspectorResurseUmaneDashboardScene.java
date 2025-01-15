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
import java.sql.ResultSet;
import java.sql.SQLException;

public class InspectorResurseUmaneDashboardScene {
    private Scene scene;

    public InspectorResurseUmaneDashboardScene(Stage primaryStage, Connection connection) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;"); // Background albastru

        // Title
        Label titleLabel = new Label("INSPECTOR RESURSE UMANE");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GRAY);

        // Detalii personale Section
        Label personalDetailsLabel = new Label("Detalii personale:");
        personalDetailsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Create and add personal details to the root container
        VBox personalDetailsBox = createPersonalDataBox(connection);

        // Orar angajat Section
        Label scheduleLabel = new Label("Orar angajat:");
        scheduleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ListView<String> employeeScheduleListView = new ListView<>();

        // Add sections to the root container
        root.getChildren().addAll(titleLabel, personalDetailsLabel, personalDetailsBox, scheduleLabel, employeeScheduleListView);

        // Search Employee Section
        Label searchLabel = new Label("Caută angajat:");
        searchLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField searchNameField = new TextField();
        searchNameField.setPromptText("Introduceți numele");

        TextField searchSurnameField = new TextField();
        searchSurnameField.setPromptText("Introduceți prenumele");

        Button searchButton = new Button("Caută");
        searchButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");

        ListView<String> employeeDetailsListView = new ListView<>();
        searchButton.setOnAction(e -> searchEmployee(connection, searchNameField.getText(), searchSurnameField.getText(), employeeDetailsListView, employeeScheduleListView));

        HBox searchBox = new HBox(10, searchNameField, searchSurnameField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #FF6347; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });

        buttonBox.getChildren().add(logoutButton);

        // Add search section and button box to the root container
        root.getChildren().addAll(searchLabel, searchBox, employeeDetailsListView, buttonBox);

        this.scene = new Scene(root, 900, 700);
    }

    private VBox createPersonalDataBox(Connection connection) {
        VBox personalDataBox = new VBox(10);
        personalDataBox.setAlignment(Pos.CENTER_LEFT);
        personalDataBox.setStyle("-fx-background-color: #F0F8FF; -fx-padding: 20px;");

        Label cnpLabel = new Label("CNP: ");
        Label numeLabel = new Label("Nume: ");
        Label prenumeLabel = new Label("Prenume: ");
        Label contactLabel = new Label("Contact: ");
        Label emailLabel = new Label("Email: ");
        Label adresaLabel = new Label("Adresă: ");
        Label ibanLabel = new Label("IBAN: ");

        loadPersonalData(connection, cnpLabel, numeLabel, prenumeLabel, contactLabel, emailLabel, adresaLabel, ibanLabel);

        personalDataBox.getChildren().addAll(cnpLabel, numeLabel, prenumeLabel, contactLabel, emailLabel, adresaLabel, ibanLabel);
        return personalDataBox;
    }

    private void loadPersonalData(Connection connection, Label... labels) {
        String loggedInCNP = Session.getLoggedUserCNP();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM utilizator WHERE CNP = ?")) {
            stmt.setString(1, loggedInCNP);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                labels[0].setText("CNP: " + resultSet.getString("CNP"));
                labels[1].setText("Nume: " + resultSet.getString("nume"));
                labels[2].setText("Prenume: " + resultSet.getString("prenume"));
                labels[3].setText("Contact: " + resultSet.getString("contact"));
                labels[4].setText("Email: " + resultSet.getString("email"));
                labels[5].setText("Adresă: " + resultSet.getString("adresa"));
                labels[6].setText("IBAN: " + resultSet.getString("IBAN"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading personal data: " + e.getMessage());
        }
    }

    private void searchEmployee(Connection connection, String name, String surname, ListView<String> detailsListView, ListView<String> scheduleListView) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        String query = "SELECT a.id_angajat, u.nume, u.prenume, a.ore_lucrate, a.salariu_negociat FROM Utilizator u " +
                "JOIN Angajati a ON u.CNP = a.CNP WHERE u.nume = ? AND u.prenume = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            ResultSet rs = stmt.executeQuery();

            detailsListView.getItems().clear();
            scheduleListView.getItems().clear();

            if (rs.next()) {
                int employeeId = rs.getInt("id_angajat");
                String details = String.format("ID: %d, Nume: %s %s, Ore lucrate: %d, Salariu negociat: %.2f",
                        employeeId, rs.getString("nume"), rs.getString("prenume"),
                        rs.getInt("ore_lucrate"), rs.getDouble("salariu_negociat"));
                detailsListView.getItems().add(details);

                loadEmployeeSchedule(connection, employeeId, scheduleListView);
                loadEmployeeLeave(connection, employeeId, detailsListView);
            } else {
                detailsListView.getItems().add("Nu a fost găsit niciun angajat cu acest nume și prenume.");
            }
        } catch (SQLException e) {
            detailsListView.getItems().add("Eroare la căutarea angajatului.");
            e.printStackTrace();
        }
    }

    private void loadEmployeeSchedule(Connection connection, int employeeId, ListView<String> scheduleListView) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        String query = "SELECT zi_saptamana, data_specifica, ora_inceput, ora_sfarsit FROM OrarAngajati WHERE id_angajat = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            scheduleListView.getItems().clear();
            while (rs.next()) {
                String schedule = String.format("Zi: %s, Data: %s, Interval: %s - %s",
                        rs.getString("zi_saptamana"),
                        rs.getDate("data_specifica") != null ? rs.getDate("data_specifica").toString() : "N/A",
                        rs.getString("ora_inceput"), rs.getString("ora_sfarsit"));
                scheduleListView.getItems().add(schedule);
            }
        } catch (SQLException e) {
            scheduleListView.getItems().add("Eroare la încărcarea orarului.");
            e.printStackTrace();
        }
    }

    private void loadEmployeeLeave(Connection connection, int employeeId, ListView<String> detailsListView) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        String query = "SELECT data_inceput, data_sfarsit, motiv FROM ConcediiAngajati WHERE id_angajat = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            detailsListView.getItems().add("Concedii:");
            while (rs.next()) {
                String leave = String.format("De la: %s până la: %s, Motiv: %s",
                        rs.getDate("data_inceput"), rs.getDate("data_sfarsit"), rs.getString("motiv"));
                detailsListView.getItems().add(leave);
            }
        } catch (SQLException e) {
            detailsListView.getItems().add("Eroare la încărcarea concediilor.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Scene getScene() {
        return this.scene;
    }
}