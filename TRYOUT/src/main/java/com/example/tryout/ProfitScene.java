package com.example.tryout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfitScene {
    private Scene scene;
    private Connection connection;

    public ProfitScene(Stage primaryStage, Connection connection) {
        this.connection = connection;

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        // Setăm fișierul CSS
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Adăugăm butoanele pentru selecția tipului de profit
        Button profitByMediciButton = new Button("Profit pe Medici");
        profitByMediciButton.getStyleClass().add("button"); // Aplicăm stilul butonului
        profitByMediciButton.setOnAction(e -> showProfitByMedici(primaryStage));

        Button profitByUnitatiButton = new Button("Profit pe Unități Medicale");
        profitByUnitatiButton.getStyleClass().add("button");
        profitByUnitatiButton.setOnAction(e -> showProfitByUnitati(primaryStage, connection));

        Button profitBySpecialitateButton = new Button("Profit pe Specialitate");
        profitBySpecialitateButton.getStyleClass().add("button");
        profitBySpecialitateButton.setOnAction(e -> showProfitBySpecialitate(primaryStage, connection));

        // Adăugăm un buton pentru a merge înapoi la dashboard
        Button backButton = new Button("Înapoi");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> {
            ExpertContabilDashboardScene dashboardScene = new ExpertContabilDashboardScene(primaryStage, connection);
            primaryStage.setScene(dashboardScene.getScene());
        });

        // Adăugăm butoanele la containerul principal
        VBox buttonBox = new VBox(10, profitByMediciButton, profitByUnitatiButton, profitBySpecialitateButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        root.getChildren().addAll(buttonBox, backButton);
        this.scene = new Scene(root, 900, 700);
    }

    private void showProfitByMedici(Stage primaryStage) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "SELECT NumeMedic, VenituriGenerale - SalariuCalculat AS Profit FROM ProfitPeMedic";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            VBox resultBox = new VBox(10);
            resultBox.setPadding(new Insets(20));

            while (rs.next()) {
                String result = String.format("Medic: %s, Profit: %.2f", rs.getString("NumeMedic"), rs.getDouble("Profit"));
                resultBox.getChildren().add(new Label(result));
            }

            Button backButton = new Button("Back");
            backButton.setOnAction(e -> showVenitSelection(primaryStage));  // Înapoi la selecția venit

            resultBox.getChildren().add(backButton);

            Scene resultScene = new Scene(resultBox, 600, 400);
            primaryStage.setScene(resultScene);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "A apărut o eroare la încărcarea profitului pe medici.");
            e.printStackTrace();
        }
    }

    private void showProfitByUnitati(Stage primaryStage, Connection connection) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "SELECT nume_unitate, Venituri - Cheltuieli AS Profit FROM ProfitPeUnitate";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            VBox resultBox = new VBox(10);
            resultBox.setPadding(new Insets(20));

            while (rs.next()) {
                String result = String.format("Unitate: %s, Profit: %.2f", rs.getString("nume_unitate"), rs.getDouble("Profit"));
                resultBox.getChildren().add(new Label(result));
            }

            Button backButton = new Button("Back");
            backButton.setOnAction(e -> showVenitSelection(primaryStage));  // Înapoi la selecția venit

            resultBox.getChildren().add(backButton);

            Scene resultScene = new Scene(resultBox, 600, 400);
            primaryStage.setScene(resultScene);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "A apărut o eroare la încărcarea profitului pe unități medicale.");
            e.printStackTrace();
        }
    }

    private void showProfitBySpecialitate(Stage primaryStage, Connection connection) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        // Interogarea SQL conform cerinței tale
        String query = "SELECT m.specialitate AS Specialitate, " +
                "SUM(sm.pret) AS VenituriGenerale, " +
                "SUM(sm.pret * (m.procent_servicii / 100)) AS ComisionNegociat, " +
                "SUM(a.salariu_negociat * (a.ore_lucrate / 160)) AS SalariuCalculat, " +
                "(SUM(sm.pret * (m.procent_servicii / 100))) - (SUM(a.salariu_negociat * (a.ore_lucrate / 160))) AS Profit " +
                "FROM Medic m " +
                "JOIN Angajati a ON m.id_angajat = a.id_angajat " +
                "JOIN ServiciiMedicale sm ON sm.id_medic = m.id_medic " +
                "JOIN Programari p ON p.id_serviciu = sm.id_serviciu " +
                "WHERE p.status IN ('planificata', 'realizata') " +
                "GROUP BY m.specialitate";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            VBox resultBox = new VBox(10);
            resultBox.setPadding(new Insets(20));

            while (rs.next()) {
                // Obține datele din ResultSet și creează textul de rezultat
                String result = String.format("Specialitate: %s, Venituri Generale: %.2f, Comision Negociat: %.2f, Salarii Calculate: %.2f, Profit: %.2f",
                        rs.getString("Specialitate"),
                        rs.getDouble("VenituriGenerale"),
                        rs.getDouble("ComisionNegociat"),
                        rs.getDouble("SalariuCalculat"),
                        rs.getDouble("Profit"));
                resultBox.getChildren().add(new Label(result));
            }

            // Butonul pentru a reveni la ecranul anterior
            Button backButton = new Button("Back");
            backButton.setOnAction(e -> showVenitSelection(primaryStage));  // Înapoi la selecția venit

            resultBox.getChildren().add(backButton);

            // Crearea și setarea scenei
            Scene resultScene = new Scene(resultBox, 600, 400);
            primaryStage.setScene(resultScene);
        } catch (SQLException e) {
            // Afișează eroare în caz de problemă la interogare
            showAlert(Alert.AlertType.ERROR, "Eroare", "A apărut o eroare la încărcarea profitului pe specialitate.");
            e.printStackTrace();
        }
    }

    private void showVenitSelection(Stage primaryStage) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        VBox selectionBox = new VBox(20);
        selectionBox.setPadding(new Insets(20));

        Button btnMedici = new Button("Profit pe Medici");
        btnMedici.setOnAction(e -> showProfitByMedici(primaryStage));

        Button btnUnitati = new Button("Profit pe Unități");
        btnUnitati.setOnAction(e -> showProfitByUnitati(primaryStage, connection));

        Button btnSpecialitate = new Button("Profit pe Specialitate");
        btnSpecialitate.setOnAction(e -> showProfitBySpecialitate(primaryStage, connection));

        selectionBox.getChildren().addAll(btnMedici, btnUnitati, btnSpecialitate);

        Scene selectionScene = new Scene(selectionBox, 600, 400);
        primaryStage.setScene(selectionScene);
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
