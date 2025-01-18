package com.example.tryout;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AsistentMedicalDashboardScene {
    private Scene scene;
    private String cnpAsistent = Session.getLoggedUserCNP();

    public AsistentMedicalDashboardScene(Stage primaryStage, Connection connection) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;");

        // Titlu
        Label titleLabel = new Label("ASISTENT MEDICAL");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GRAY);

        // Tabelul pentru programări
        TableView<Appointment> appointmentsTable = new TableView<>();
        configureTableColumns(appointmentsTable);

        // Câmp de căutare pacient
        TextField searchPatientField = new TextField();
        searchPatientField.setPromptText("Introduceți CNP-ul pacientului");

        // Buton pentru căutarea pacientului
        Button searchButton = new Button("Căutare pacient");
        searchButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");

        searchButton.setOnAction(e -> {
            String cnpClient = searchPatientField.getText();
            searchPatientAndDisplayAppointments(connection, cnpClient, appointmentsTable);
        });

        // Secțiunea de date utilizator
        Label userSectionLabel = new Label("Date utilizator");
        userSectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField nameField = new TextField();
        nameField.setPromptText("Nume");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Prenume");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField addressField = new TextField();
        addressField.setPromptText("Adresă");

        TextField ibanField = new TextField();
        ibanField.setPromptText("IBAN");

        Button saveButton = new Button("Salvează modificările");
        saveButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4682B4; -fx-text-fill: white;");

        saveButton.setOnAction(e -> {
            saveUserData(connection, nameField.getText(), surnameField.getText(), contactField.getText(),
                    emailField.getText(), addressField.getText(), ibanField.getText());
        });

        // Butonul Logout
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #FF6347; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            Session.setLoggedUserCNP("");
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });

        // Încărcăm datele utilizatorului
        loadUserData(connection, nameField, surnameField, contactField, emailField, addressField, ibanField);

        // Adăugăm componentele în root
        root.getChildren().addAll(titleLabel, searchPatientField, searchButton, appointmentsTable, userSectionLabel,
                nameField, surnameField, contactField, emailField, addressField, ibanField, saveButton, logoutButton);

        this.scene = new Scene(root, 1000, 800);
    }

    public Scene getScene() {
        return this.scene;
    }

    private void loadUserData(Connection connection, TextField nameField, TextField surnameField, TextField contactField,
                              TextField emailField, TextField addressField, TextField ibanField) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "SELECT Nume, Prenume, Contact, Email, Adresa, IBAN FROM Utilizator WHERE CNP = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cnpAsistent);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("Nume"));
                surnameField.setText(rs.getString("Prenume"));
                contactField.setText(rs.getString("Contact"));
                emailField.setText(rs.getString("Email"));
                addressField.setText(rs.getString("Adresa"));
                ibanField.setText(rs.getString("IBAN"));
            } else {
                System.out.println("Nu s-au găsit datele utilizatorului.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea datelor utilizatorului: " + e.getMessage());
        }
    }

    private void saveUserData(Connection connection, String name, String surname, String contact, String email,
                              String address, String iban) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "UPDATE Utilizator SET Nume = ?, Prenume = ?, Contact = ?, Email = ?, Adresa = ?, IBAN = ? WHERE CNP = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, contact);
            stmt.setString(4, email);
            stmt.setString(5, address);
            stmt.setString(6, iban);
            stmt.setString(7, cnpAsistent);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Datele utilizatorului au fost actualizate cu succes.");
            } else {
                System.out.println("Nu s-a reușit actualizarea datelor.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea datelor utilizatorului: " + e.getMessage());
        }
    }

    private void searchPatientAndDisplayAppointments(Connection connection, String cnpClient, TableView<Appointment> programariTable) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement("SELECT id_client, nume_client, prenume_client FROM Clienti WHERE CNP = ?")) {
            stmt.setString(1, cnpClient);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idClient = rs.getInt("id_client");
                String sqlProgramari = """
                        SELECT p.data_programare, p.ora_programare, s.nume_serviciu, p.status
                        FROM Programari p
                        JOIN ServiciiMedicale s ON p.id_serviciu = s.id_serviciu
                        WHERE p.id_client = ?
                        """;

                try (PreparedStatement stmtProgramari = connection.prepareStatement(sqlProgramari)) {
                    stmtProgramari.setInt(1, idClient);
                    ResultSet rsProgramari = stmtProgramari.executeQuery();

                    programariTable.getItems().clear();

                    while (rsProgramari.next()) {
                        Appointment programare = new Appointment(
                                rsProgramari.getString("data_programare"),
                                rsProgramari.getString("ora_programare"),
                                rsProgramari.getString("nume_serviciu"),
                                rsProgramari.getString("status")
                        );
                        programariTable.getItems().add(programare);
                    }
                }
            } else {
                System.out.println("Nu s-a găsit pacient cu acest CNP.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la căutarea pacientului: " + e.getMessage());
        }
    }

    private void configureTableColumns(TableView<Appointment> tableView) {
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Data");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData_programare()));

        TableColumn<Appointment, String> timeColumn = new TableColumn<>("Ora");
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOra_programare()));

        TableColumn<Appointment, String> serviceColumn = new TableColumn<>("Serviciu");
        serviceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiciu()));

        TableColumn<Appointment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        tableView.getColumns().addAll(dateColumn, timeColumn, serviceColumn, statusColumn);
    }

    private boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
