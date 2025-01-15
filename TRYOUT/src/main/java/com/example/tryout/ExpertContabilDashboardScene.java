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

public class ExpertContabilDashboardScene {
    private Scene scene;

    public ExpertContabilDashboardScene(Stage primaryStage, Connection connection) {
        VBox root = createRootLayout();

        Label titleLabel = createTitleLabel();
        VBox personalDetailsBox = createPersonalDataBox(connection);
        Label scheduleLabel = createSectionLabel("Orar angajat:");
        ListView<String> employeeScheduleListView = new ListView<>();
        ListView<String> employeeDetailsListView = new ListView<>();

        root.getChildren().addAll(titleLabel, createSectionLabel("Detalii personale:"), personalDetailsBox, scheduleLabel, employeeScheduleListView);

        HBox searchBox = createSearchSection(connection, employeeDetailsListView, employeeScheduleListView);
        Button profitButton = createProfitButton(primaryStage, connection);
        Button logoutButton = createLogoutButton(primaryStage);
        HBox buttonBox = createButtonBox(logoutButton, profitButton);

        root.getChildren().addAll(createSectionLabel("Caută angajat:"), searchBox, employeeDetailsListView, buttonBox);

        this.scene = new Scene(root, 900, 700);
    }

    private VBox createRootLayout() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;");
        return root;
    }

    private Label createTitleLabel() {
        Label titleLabel = new Label("EXPERT CONTABIL");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GRAY);
        return titleLabel;
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        return label;
    }

    private VBox createPersonalDataBox(Connection connection) {
        VBox personalDataBox = new VBox(10);
        personalDataBox.setAlignment(Pos.CENTER_LEFT);
        personalDataBox.setStyle("-fx-background-color: #F0F8FF; -fx-padding: 20px;");

        Label[] labels = createPersonalDataLabels();
        loadPersonalData(connection, labels);

        Button refreshButton = createButton("Reîncarcă", "#4682B4", e -> loadPersonalData(connection, labels));
        Button updateButton = createButton("Actualizează Detalii Personale", "#4682B4", e -> openUpdatePersonalDetailsDialog(connection, labels));

        personalDataBox.getChildren().addAll(labels);
        personalDataBox.getChildren().addAll(refreshButton, updateButton);
        return personalDataBox;
    }

    private Label[] createPersonalDataLabels() {
        return new Label[] {
                new Label("CNP: "),
                new Label("Nume: "),
                new Label("Prenume: "),
                new Label("Contact: "),
                new Label("Email: "),
                new Label("Adresă: "),
                new Label("IBAN: "),
                new Label("Salariu: ")
        };
    }


    private HBox createSearchSection(Connection connection, ListView<String> detailsListView, ListView<String> scheduleListView) {
        TextField searchNameField = new TextField();
        searchNameField.setPromptText("Introduceți numele");

        TextField searchSurnameField = new TextField();
        searchSurnameField.setPromptText("Introduceți prenumele");

        Button searchButton = createButton("Caută", "#32CD32", e -> searchEmployee(connection, searchNameField.getText(), searchSurnameField.getText(), detailsListView, scheduleListView));

        HBox searchBox = new HBox(10, searchNameField, searchSurnameField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        return searchBox;
    }

    private Button createProfitButton(Stage primaryStage, Connection connection) {
        return createButton("Vezi Profit", "#32CD32", e -> {
            ProfitScene profitScene = new ProfitScene(primaryStage, connection);
            primaryStage.setScene(profitScene.getScene());
        });
    }

    private Button createLogoutButton(Stage primaryStage) {
        return createButton("Logout", "#FF6347", e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });
    }

    private HBox createButtonBox(Button... buttons) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(buttons);
        return buttonBox;
    }

    private Button createButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: %s; -fx-text-fill: white;", color));
        button.setOnAction(action);
        return button;
    }

    private void openUpdatePersonalDetailsDialog(Connection connection, Label... labels) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Actualizează Detalii Personale");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        TextField contactField = new TextField();
        contactField.setPromptText("Contact nou");

        TextField emailField = new TextField();
        emailField.setPromptText("Email nou");

        TextField addressField = new TextField();
        addressField.setPromptText("Adresă nouă");

        TextField ibanField = new TextField();
        ibanField.setPromptText("IBAN nou");

        Connection finalConnection = connection;
        Button saveButton = createButton("Salvează", "#32CD32", e -> {
            updatePersonalDetails(finalConnection, contactField.getText(), emailField.getText(), addressField.getText(), ibanField.getText());
            loadPersonalData(finalConnection, labels);
            dialogStage.close();
        });

        Button cancelButton = createButton("Anulează", "#FF6347", e -> dialogStage.close());
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        dialogVBox.getChildren().addAll(contactField, emailField, addressField, ibanField, buttonBox);

        Scene dialogScene = new Scene(dialogVBox, 400, 300);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }



    private void loadPersonalData(Connection connection, Label... labels) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String loggedInCNP = Session.getLoggedUserCNP();
        String query = "SELECT u.CNP, u.nume, u.prenume, u.contact, u.email, u.adresa, u.IBAN, a.salariu_negociat, a.ore_lucrate " +
                "FROM utilizator u " +
                "JOIN angajati a ON u.CNP = a.CNP " +
                "WHERE u.CNP = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, loggedInCNP);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                labels[0].setText("CNP: " + resultSet.getString("CNP"));
                labels[1].setText("Nume: " + resultSet.getString("nume"));
                labels[2].setText("Prenume: " + resultSet.getString("prenume"));
                labels[3].setText("Contact: " + resultSet.getString("contact"));
                labels[4].setText("Email: " + resultSet.getString("email"));
                labels[5].setText("Adresă: " + resultSet.getString("adresa"));
                labels[6].setText("IBAN: " + resultSet.getString("IBAN"));
                labels[7].setText(String.format("Salariu: %.2f, Ore lucrate: %d",
                        resultSet.getDouble("salariu_negociat"), resultSet.getInt("ore_lucrate")));
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

        String query = "SELECT a.id_angajat, u.nume, u.prenume, a.ore_lucrate, a.salariu_negociat FROM utilizator u " +
                "JOIN angajati a ON u.CNP = a.CNP WHERE u.nume = ? AND u.prenume = ?";

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

    private void updatePersonalDetails(Connection connection, String newContact, String newEmail, String newAddress, String newIban) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String loggedInCNP = Session.getLoggedUserCNP();

        String updateQuery = "UPDATE utilizator SET contact = ?, email = ?, adresa = ?, IBAN = ? WHERE CNP = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setString(1, newContact);
            stmt.setString(2, newEmail);
            stmt.setString(3, newAddress);
            stmt.setString(4, newIban);
            stmt.setString(5, loggedInCNP);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Detaliile personale au fost actualizate cu succes.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-au putut actualiza detaliile personale.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "A apărut o eroare la actualizarea detaliilor personale.");
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return this.scene;
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
