package com.example.tryout;
import javafx.scene.control.cell.PropertyValueFactory;

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

public class ClientDashboardScene {
    private Scene scene;

    public ClientDashboardScene(Stage primaryStage, Connection connection) {

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;"); // Background albastru


        // Title
        Label titleLabel = new Label("PACIENT");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GRAY);

        // Personal Details Section
        Label personalDetailsLabel = new Label("Detalii personale:");
        personalDetailsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label personalDetails = new Label();
        personalDetails.setFont(Font.font("Arial", 14));
        personalDetails.setWrapText(true);

        // Current Appointment Section
        Label currentAppointmentLabel = new Label("Programare curenta:");
        currentAppointmentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label currentAppointment = new Label();
        currentAppointment.setFont(Font.font("Arial", 14));
        currentAppointment.setWrapText(true);

        // Appointment History Section
        Label historyLabel = new Label("Istoricul programarilor:");
        historyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        TableView<Appointment> historyTable = new TableView<>();

        // Columns for TableView
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Data programarii");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("data_programare"));

        TableColumn<Appointment, String> timeColumn = new TableColumn<>("Ora programarii");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("ora_programare"));

        TableColumn<Appointment, String> serviceColumn = new TableColumn<>("Servicii medicale");
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("serviciu"));

        TableColumn<Appointment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        historyTable.getColumns().addAll(dateColumn, timeColumn, serviceColumn, statusColumn);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load Data
        String clientCNP = Session.getLoggedUserCNP();
        loadPersonalDetails(connection, clientCNP, personalDetails);
        loadCurrentAppointment(connection, clientCNP, currentAppointment);
        loadAppointmentHistory(connection, clientCNP, historyTable);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);


        Button editDetailsButton = new Button("Editeaza date");
        editDetailsButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");
        editDetailsButton.setOnAction(e -> openEditDetailsForm(primaryStage, connection, clientCNP));

        Button logoutButton = new Button("Logout");
        // Reset session
        Session.setLoggedUserCNP("");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #FF6347; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });


        buttonBox.getChildren().addAll(editDetailsButton, logoutButton);

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
                currentAppointment.setText("Nu exista programare.");
            }
        } catch (Exception e) {
            currentAppointment.setText("Eroare la incarcare.");
            e.printStackTrace();
        }
    }

    private void openEditDetailsForm(Stage primaryStage, Connection connection, String clientCNP) {
        VBox editRoot = new VBox(15);
        editRoot.setPadding(new Insets(20));
        editRoot.setStyle("-fx-background-color: #F0F8FF;");
        editRoot.setAlignment(Pos.CENTER);

        Label editTitle = new Label("Editeaza detalii personale");
        editTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField nameField = new TextField();
        nameField.setPromptText("Nume");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Prenume");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField addressField = new TextField();
        addressField.setPromptText("Adresa");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact");



        Button saveButton = new Button("Salveaza");
        saveButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() ||
                    emailField.getText().isEmpty() || addressField.getText().isEmpty() || contactField.getText().isEmpty()) {
                System.out.println("Toate câmpurile trebuie completate!");
                return;
            }
            updatePersonalDetails(connection, clientCNP, nameField.getText(), surnameField.getText(),
                    emailField.getText(), addressField.getText(), contactField.getText());
            primaryStage.setScene(this.scene); // Revenire la dashboard
        });


        Button cancelButton = new Button("Inchide");
        cancelButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> primaryStage.setScene(this.scene));

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);



        editRoot.getChildren().addAll(editTitle, nameField, surnameField, emailField, addressField, contactField, buttonBox);

        Scene editScene = new Scene(editRoot, 600, 400);
        primaryStage.setScene(editScene);
    }

    private void updatePersonalDetails(Connection connection, String clientCNP, String nume, String prenume, String email, String adresa, String contact) {
        // Verifică dacă conexiunea este validă
        try {
            if (connection == null || connection.isClosed()) {
                connection = MyConnection.getConnection();  // Redeschide conexiunea
                System.out.println("Conexiune redeschisă.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la redeschiderea conexiunii: " + e.getMessage());
            return; // Dacă nu reușește să redeschidă conexiunea, ieși din metodă
        }

        // Log pentru a verifica ce date sunt transmise
        System.out.println("Actualizare detalii: CNP=" + clientCNP + ", Nume=" + nume + ", Prenume=" + prenume + ", Email=" + email + ", Adresa=" + adresa + ", Contact=" + contact);

        String query = "UPDATE Clienti SET nume = ?, prenume = ?, email = ?, adresa = ?, contact = ? WHERE CNP = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nume);
            stmt.setString(2, prenume);
            stmt.setString(3, email);
            stmt.setString(4, adresa);
            stmt.setString(5, contact);
            stmt.setString(6, clientCNP);

            // Execută interogarea și verifică câte rânduri au fost actualizate
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Detaliile au fost actualizate cu succes.");
            } else {
                System.out.println("Niciun rând nu a fost actualizat. Verifică dacă CNP-ul este corect.");
            }
        } catch (SQLException e) {
            // Log detaliat pentru erori
            System.err.println("Eroare la actualizarea detaliilor personale: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void loadPersonalDetails(Connection connection, String clientCNP, Label personalDetails) {
        String query = "SELECT nume_client, prenume_client, contact, email, adresa FROM Clienti WHERE CNP = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String details = String.format("Nume: %s %s\nEmail: %s\nAdresa: %s\nContact: %s",
                        rs.getString("nume_client"),
                        rs.getString("prenume_client"),
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
}
