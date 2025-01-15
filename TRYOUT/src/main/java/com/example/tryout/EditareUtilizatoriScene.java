package com.example.tryout;

import com.example.tryout.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditareUtilizatoriScene {
    private Scene scene;

    public EditareUtilizatoriScene(Stage primaryStage, Connection connection) {
        GridPane root = new GridPane();
        root.setPadding(new Insets(20));
        root.setHgap(10);
        root.setVgap(10);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label titleLabel = new Label("Modificare Utilizatori");
        titleLabel.setFont(Font.font("Brush Script MT", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.getStyleClass().add("label-title");
        GridPane.setColumnSpan(titleLabel, 2);
        root.add(titleLabel, 0, 0);

        // TableView pentru afișarea utilizatorilor
        TableView<User> tableView = new TableView<>();
        tableView.setPrefHeight(300); // Dimensiune mărită
        tableView.setPrefWidth(700);  // Dimensiune mărită
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> cnpColumn = new TableColumn<>("CNP");
        cnpColumn.setCellValueFactory(new PropertyValueFactory<>("cnp"));

        TableColumn<User, Integer> oreColumn = new TableColumn<>("Ore lucrate");
        oreColumn.setCellValueFactory(new PropertyValueFactory<>("oreLucrate"));

        TableColumn<User, Double> salariuColumn = new TableColumn<>("Salariu negociat");
        salariuColumn.setCellValueFactory(new PropertyValueFactory<>("salariuNegociat"));

        TableColumn<User, Integer> idUnitateColumn = new TableColumn<>("ID Unitate");
        idUnitateColumn.setCellValueFactory(new PropertyValueFactory<>("idUnitate"));

        TableColumn<User, String> tipUtilizatorColumn = new TableColumn<>("Tip utilizator");
        tipUtilizatorColumn.setCellValueFactory(new PropertyValueFactory<>("tipUtilizator"));

        tableView.getColumns().addAll(cnpColumn, oreColumn, salariuColumn, idUnitateColumn, tipUtilizatorColumn);
        root.add(tableView, 0, 1, 2, 1);

        // Formular pentru modificarea datelor
        Label cnpLabel = new Label("CNP:");
        TextField cnpField = new TextField();
        cnpField.setEditable(false); // CNP-ul nu poate fi modificat

        Label oreLabel = new Label("Ore lucrate:");
        TextField oreField = new TextField();

        Label salariuLabel = new Label("Salariu negociat:");
        TextField salariuField = new TextField();

        Label idUnitateLabel = new Label("ID Unitate:");
        TextField idUnitateField = new TextField();

        Label tipUtilizatorLabel = new Label("Tip utilizator:");
        ComboBox<String> tipUtilizatorComboBox = new ComboBox<>();
        tipUtilizatorComboBox.getItems().addAll("admin", "super-admin", "angajat");

        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(10));
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.add(cnpLabel, 0, 0);
        formPane.add(cnpField, 1, 0);
        formPane.add(oreLabel, 0, 1);
        formPane.add(oreField, 1, 1);
        formPane.add(salariuLabel, 0, 2);
        formPane.add(salariuField, 1, 2);
        formPane.add(idUnitateLabel, 0, 3);
        formPane.add(idUnitateField, 1, 3);
        formPane.add(tipUtilizatorLabel, 0, 4);
        formPane.add(tipUtilizatorComboBox, 1, 4);
        root.add(formPane, 0, 2, 2, 1);

        // Buton pentru încărcarea datelor
        Button loadButton = new Button("Încărcați datele");
        loadButton.getStyleClass().add("button-login");
        loadButton.setOnAction(e -> loadUserData(connection, tableView));

        // Buton pentru salvarea modificărilor
        Button saveButton = new Button("Salvează modificările");
        saveButton.getStyleClass().add("button-signup");
        saveButton.setOnAction(e -> updateUser(connection, tableView, cnpField, oreField, salariuField, idUnitateField, tipUtilizatorComboBox));

        // Buton Înapoi
        Button backButton = new Button("Înapoi");
        backButton.getStyleClass().add("button-login");
        backButton.setOnAction(e -> {
            InspectorResurseUmaneDashboardScene dashboardScene = new InspectorResurseUmaneDashboardScene(primaryStage, connection);
            primaryStage.setScene(dashboardScene.getScene());
        });

        HBox buttonBox = new HBox(10, loadButton, saveButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.add(buttonBox, 0, 3, 2, 1);

        // Selectarea unui utilizator din tabel
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cnpField.setText(newSelection.getCnp());
                oreField.setText(String.valueOf(newSelection.getOreLucrate()));
                salariuField.setText(String.valueOf(newSelection.getSalariuNegociat()));
                idUnitateField.setText(String.valueOf(newSelection.getIdUnitate()));
                tipUtilizatorComboBox.setValue(newSelection.getTipUtilizator());
            }
        });

        // Setăm scena
        this.scene = new Scene(root, 900, 700); // Dimensiuni mărită
        this.scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

    }

    private void updateUser(Connection connection, TableView<User> tableView, TextField cnpField, TextField oreField, TextField salariuField, TextField idUnitateField, ComboBox<String> tipUtilizatorComboBox) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        String cnp = cnpField.getText();
        String ore = oreField.getText();
        String salariu = salariuField.getText();
        String idUnitate = idUnitateField.getText();
        String tipUtilizator = tipUtilizatorComboBox.getValue();

        if (cnp.isEmpty() || ore.isEmpty() || salariu.isEmpty() || idUnitate.isEmpty() || tipUtilizator == null) {
            showAlert1(Alert.AlertType.ERROR, "Toate câmpurile trebuie completate!");
            return;
        }

        try {
            String updateUtilizatorQuery = "UPDATE Utilizator SET id_unitate = ?, tip_utilizator = ? WHERE CNP = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateUtilizatorQuery)) {
                stmt.setInt(1, Integer.parseInt(idUnitate));
                stmt.setString(2, tipUtilizator);
                stmt.setString(3, cnp);
                stmt.executeUpdate();
            }

            String updateAngajatiQuery = "UPDATE Angajati SET ore_lucrate = ?, salariu_negociat = ? WHERE CNP = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateAngajatiQuery)) {
                stmt.setInt(1, Integer.parseInt(ore));
                stmt.setDouble(2, Double.parseDouble(salariu));
                stmt.setString(3, cnp);
                stmt.executeUpdate();
            }

            loadUserData(connection, tableView); // Reîmprospătăm tabelul
            showAlert1(Alert.AlertType.INFORMATION, "Modificările au fost salvate cu succes!");
        } catch (SQLException | NumberFormatException e) {
            showAlert1(Alert.AlertType.ERROR, "Eroare la actualizare: " + e.getMessage());
        }
    }

    private void showAlert1(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadUserData(Connection connection, TableView<User> tableView) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = """
                SELECT u.CNP, u.id_unitate, u.tip_utilizator, a.ore_lucrate, a.salariu_negociat
                FROM Utilizator u
                JOIN Angajati a ON u.CNP = a.CNP
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("CNP"),
                        rs.getInt("ore_lucrate"),
                        rs.getDouble("salariu_negociat"),
                        rs.getInt("id_unitate"),
                        rs.getString("tip_utilizator")
                ));
            }

            tableView.setItems(users);
        } catch (SQLException e) {
            showAlert1(Alert.AlertType.ERROR, "Eroare la încărcarea datelor: " + e.getMessage());
        }
    }

    private boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }




    public Scene getScene() {
        return scene;
    }
}
