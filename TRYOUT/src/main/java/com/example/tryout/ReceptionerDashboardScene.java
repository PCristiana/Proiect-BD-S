// merge conectarea, se salveaza conectarile, mai e nevoie d earanjare in pagina si sa faci bonurile si sa mai adaugi ceva cand afisezi pacientii
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
import java.time.LocalDate;

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
        Label titleLabel = new Label("Receptioner");
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

        Button editPersonalDataButton = new Button("Editează datele personale");
        editPersonalDataButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        Connection finalConnection2 = connection;
        editPersonalDataButton.setOnAction(e -> {
            openEditPersonalDataForm(primaryStage, finalConnection2);
        });
        buttonBox.getChildren().add(editPersonalDataButton);

// Crearea unui panou pentru datele personale
        VBox personalDataBox = new VBox(10);
        personalDataBox.setAlignment(Pos.CENTER_LEFT);
        personalDataBox.setStyle("-fx-background-color: #F0F8FF; -fx-padding: 20px;");

// Etichete pentru a arăta datele personale
        Label cnpLabel = new Label("CNP: ");
        Label numeLabel = new Label("Nume: ");
        Label prenumeLabel = new Label("Prenume: ");
        Label contactLabel = new Label("Contact: ");
        Label emailLabel = new Label("Email: ");
        Label adresaLabel = new Label("Adresă: ");
        Label ibanLabel = new Label("IBAN: ");

// Încărcarea datelor personale din baza de date
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM utilizator WHERE CNP = ?")) {
            stmt.setString(1, "4134567892007");  // Înlocuiește cu CNP-ul utilizatorului logat
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                cnpLabel.setText("CNP: " + resultSet.getString("CNP"));
                numeLabel.setText("Nume: " + resultSet.getString("nume"));
                prenumeLabel.setText("Prenume: " + resultSet.getString("prenume"));
                contactLabel.setText("Contact: " + resultSet.getString("contact"));
                emailLabel.setText("Email: " + resultSet.getString("email"));
                adresaLabel.setText("Adresă: " + resultSet.getString("adresa"));
                ibanLabel.setText("IBAN: " + resultSet.getString("IBAN"));

            }
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea datelor personale: " + e.getMessage());
        }

// Adăugarea etichetelor în panoul de date personale
        personalDataBox.getChildren().addAll(cnpLabel, numeLabel, prenumeLabel, contactLabel, emailLabel, adresaLabel, ibanLabel);

// Adăugarea panoului în dashboard
        root.getChildren().add(personalDataBox);

        // Buton pentru a adăuga o programare
        Button addAppointmentButton = new Button("Adaugă programare");
        addAppointmentButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        Connection finalConnection1 = connection;
        addAppointmentButton.setOnAction(e -> {
            openAddAppointmentForm(primaryStage, finalConnection1);
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
        // Verifică dacă conexiunea este validă
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection(); // Reîncarcă conexiunea
            if (connection == null || !isConnectionValid(connection)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        VBox formRoot = new VBox(15);
        formRoot.setPadding(new Insets(20));
        formRoot.setStyle("-fx-background-color: #F0F8FF;");
        formRoot.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Adauga programare");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // ListView pentru selectarea pacientului
        ListView<String> patientsListView = new ListView<>();
        patientsListView.setPrefHeight(150);
        patientsListView.setPrefWidth(300);

        // Populare ListView cu pacienți din baza de date
        try (PreparedStatement stmt = connection.prepareStatement("SELECT CNP, nume_client, prenume_client FROM Clienti")) {
            var resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String cnp = resultSet.getString("CNP");
                String nume = resultSet.getString("nume_client");
                String prenume = resultSet.getString("prenume_client");
                patientsListView.getItems().add(cnp + " - " + nume + " - " + prenume);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea listei de pacienți: " + e.getMessage());
        }

        // ComboBox pentru selectarea medicului
        ComboBox<String> doctorComboBox = new ComboBox<>();
        doctorComboBox.setPromptText("Alege medicul");
        try (PreparedStatement stmt = connection.prepareStatement("SELECT id_medic FROM Medic")) {
            var resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String doctorId = resultSet.getString("id_medic");
                doctorComboBox.getItems().add(doctorId);
            }
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea medicilor: " + e.getMessage());
        }

        // ComboBox pentru selectarea serviciilor
        ComboBox<String> serviceComboBox = new ComboBox<>();
        serviceComboBox.setPromptText("Alege serviciu");

        // Când se selectează un medic, actualizează serviciile disponibile
        Connection finalConnection = connection;
        doctorComboBox.setOnAction(e -> {
            serviceComboBox.getItems().clear();
            String selectedDoctor = doctorComboBox.getValue();
            try (PreparedStatement stmt = finalConnection.prepareStatement("SELECT nume_serviciu FROM ServiciiMedicale WHERE id_medic = ?")) {
                stmt.setString(1, selectedDoctor);
                var resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    serviceComboBox.getItems().add(resultSet.getString("nume_serviciu"));
                }
            } catch (SQLException ex) {
                System.err.println("Eroare la încărcarea serviciilor: " + ex.getMessage());
            }
        });

        // DatePicker pentru selectarea datei programării
        DatePicker appointmentDatePicker = new DatePicker();
        appointmentDatePicker.setPromptText("Alege data");
        appointmentDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now().plusDays(1))); // Restricționează doar datele ulterioare
            }
        });

        TextField appointmentTimeField = new TextField();
        appointmentTimeField.setPromptText("Ora programării");

        Button saveButton = new Button("Salvează programarea");
        saveButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        Connection finalConnection1 = connection;
        saveButton.setOnAction(e -> {
            String selectedPatient = patientsListView.getSelectionModel().getSelectedItem();
            if (selectedPatient == null) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Selectați un pacient.");
                return;
            }

            String clientCNP = selectedPatient.split(" - ")[0]; // Extrage CNP-ul din selecție
            int selectedDoctor = Integer.parseInt(doctorComboBox.getValue());

            if (selectedDoctor == -1) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Selectați un medic.");
                return;
            }

            String service = serviceComboBox.getValue();
            if (service == null) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Selectați un serviciu.");
                return;
            }

            LocalDate appointmentDate = appointmentDatePicker.getValue();
            if (appointmentDate == null) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Selectați o dată.");
                return;
            }

            String appointmentTime = appointmentTimeField.getText();
            if (appointmentTime.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Introduceți ora programării.");
                return;
            }

            addAppointment(finalConnection1, clientCNP,service, appointmentDate.toString(), appointmentTime);
            primaryStage.setScene(this.scene); // Revenire la dashboard
        });

        Button cancelButton = new Button("Anulează");
        cancelButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> primaryStage.setScene(this.scene));

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        formRoot.getChildren().addAll(formTitle, patientsListView, doctorComboBox, serviceComboBox, appointmentDatePicker, appointmentTimeField, buttonBox);

        Scene formScene = new Scene(formRoot, 600, 500);
        primaryStage.setScene(formScene);
    }

    private void addAppointment(Connection connection, String clientCNP, String service, String date, String time) {
        // Verifică conexiunea
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "INSERT INTO Programari (id_client, id_serviciu, data_programare, ora_programare, status) " +
                "VALUES ((SELECT id_client FROM Clienti WHERE CNP = ?), " +
                "(SELECT id_serviciu FROM ServiciiMedicale WHERE nume_serviciu = ?), ?, ?, 'planificata')";


        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clientCNP);  // Parametrul 1: CNP-ul pacientului
            stmt.setString(2, service);   // Parametrul 2: Serviciul selectat
            stmt.setString(3, date);      // Parametrul 4: Data programării
            stmt.setString(4, time);      // Parametrul 5: Ora programării

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
    private void openEditPersonalDataForm(Stage primaryStage, Connection connection) {
        // Verifică conexiunea
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection(); // Reîncarcă conexiunea
            if (connection == null || !isConnectionValid(connection)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        VBox formRoot = new VBox(15);
        formRoot.setPadding(new Insets(20));
        formRoot.setStyle("-fx-background-color: #F0F8FF;");
        formRoot.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Editează datele personale");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Câmpuri pentru datele personale
        TextField cnpField = new TextField();
        TextField numeField = new TextField();
        TextField prenumeField = new TextField();
        TextField contactField = new TextField();
        TextField emailField = new TextField();
        TextField adresaField = new TextField();
        TextField ibanField = new TextField();


        // Popularea câmpurilor cu datele existente ale recepționerului
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM utilizator WHERE CNP = ?")) {
            stmt.setInt(1, 1);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                cnpField.setText(resultSet.getString("CNP"));
                numeField.setText(resultSet.getString("nume"));
                prenumeField.setText(resultSet.getString("prenume"));
                contactField.setText(resultSet.getString("contact"));
                emailField.setText(resultSet.getString("email"));
                adresaField.setText(resultSet.getString("adresa"));
                ibanField.setText(resultSet.getString("IBAN"));

            }
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea datelor personale: " + e.getMessage());
        }

        // Buton pentru salvarea modificărilor
        Button saveButton = new Button("Salvează modificările");
        saveButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        Connection finalConnection = connection;
        saveButton.setOnAction(e -> {
            // Preia datele din câmpuri
            String cnp = cnpField.getText();
            String nume = numeField.getText();
            String prenume = prenumeField.getText();
            String contact = contactField.getText();
            String email = emailField.getText();
            String adresa = adresaField.getText();
            String iban = ibanField.getText();


            // Verifică dacă toate câmpurile sunt completate
            if (cnp.isEmpty() || nume.isEmpty() || prenume.isEmpty() || contact.isEmpty() || email.isEmpty() || adresa.isEmpty() || iban.isEmpty() ) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Toate câmpurile trebuie completate.");
                return;
            }

            // Actualizează datele în baza de date
            updatePersonalData(finalConnection, cnp, nume, prenume, contact, email, adresa, iban);
            primaryStage.setScene(this.scene); // Revenire la dashboard
        });

        Button cancelButton = new Button("Anulează");
        cancelButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> primaryStage.setScene(this.scene));

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        formRoot.getChildren().addAll(formTitle, cnpField, numeField, prenumeField, contactField, emailField, adresaField, ibanField, buttonBox);

        Scene formScene = new Scene(formRoot, 600, 500);
        primaryStage.setScene(formScene);
    }

    private void updatePersonalData(Connection connection, String cnp, String nume, String prenume, String contact, String email, String adresa, String iban) {
        String query = "UPDATE Recepționer SET nume = ?, prenume = ?, contact = ?, email = ?, adresa = ?, IBAN = ? WHERE CNP = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nume);
            stmt.setString(2, prenume);
            stmt.setString(3, contact);
            stmt.setString(4, email);
            stmt.setString(5, adresa);
            stmt.setString(6, iban);
            stmt.setString(8, cnp);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Datele personale au fost actualizate cu succes.");
            } else {
                System.out.println("Eroare la actualizarea datelor personale.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea datelor personale: " + e.getMessage());
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
}
