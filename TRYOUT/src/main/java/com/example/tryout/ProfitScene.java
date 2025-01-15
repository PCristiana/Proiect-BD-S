package com.example.tryout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfitScene {
    private final Scene scene;
    private final StackPane root;
    private final Stage primaryStage; // Stores the primary stage reference
    private Connection connection;

    public ProfitScene(Stage primaryStage, Connection connection) {
        this.primaryStage = primaryStage; // Assign primaryStage to the instance variable
        this.connection = connection;

        root = new StackPane();
        root.setPadding(new Insets(20));
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        VBox mainMenu = createMainMenu();
        root.getChildren().add(mainMenu);

        this.scene = new Scene(root, 900, 700);
    }

    private VBox createMainMenu() {
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);

        Button profitByMediciButton = new Button("Profit pe Medici");
        profitByMediciButton.getStyleClass().add("button");
        profitByMediciButton.setOnAction(e -> showProfitByMedici());

        Button profitByUnitatiButton = new Button("Profit pe Unități Medicale");
        profitByUnitatiButton.getStyleClass().add("button");
        profitByUnitatiButton.setOnAction(e -> showProfitByUnitati());

        Button profitBySpecialitateButton = new Button("Profit pe Specialitate");
        profitBySpecialitateButton.getStyleClass().add("button");
        profitBySpecialitateButton.setOnAction(e -> showProfitBySpecialitate());

        Button backButton = new Button("Înapoi");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> returnToDashboard()); // No need to pass primaryStage

        menuBox.getChildren().addAll(profitByMediciButton, profitByUnitatiButton, profitBySpecialitateButton, backButton);
        return menuBox;
    }

    private void showProfitByMedici() {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        VBox resultBox = new VBox(10);
        resultBox.setPadding(new Insets(20));

        String query = "SELECT NumeMedic, VenituriGenerale - SalariuCalculat AS Profit FROM ProfitPeMedic";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String result = String.format("Medic: %s, Profit: %.2f", rs.getString("NumeMedic"), rs.getDouble("Profit"));
                resultBox.getChildren().add(new Label(result));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "A apărut o eroare la încărcarea profitului pe medici.");
            e.printStackTrace();
        }

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> returnToMainMenu());
        resultBox.getChildren().add(backButton);

        updateRoot(resultBox);
    }

    private void showProfitByUnitati() {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        VBox resultBox = new VBox(10);
        resultBox.setPadding(new Insets(20));

        String query = "SELECT nume_unitate, Venituri - Cheltuieli AS Profit FROM ProfitPeUnitate";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String result = String.format("Unitate: %s, Profit: %.2f", rs.getString("nume_unitate"), rs.getDouble("Profit"));
                resultBox.getChildren().add(new Label(result));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "A apărut o eroare la încărcarea profitului pe unități medicale.");
            e.printStackTrace();
        }

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> returnToMainMenu());
        resultBox.getChildren().add(backButton);

        updateRoot(resultBox);
    }

    private void showProfitBySpecialitate() {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        VBox resultBox = new VBox(10);
        resultBox.setPadding(new Insets(20));

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

            while (rs.next()) {
                String result = String.format("Specialitate: %s, Venituri Generale: %.2f, Comision Negociat: %.2f, Salarii Calculate: %.2f, Profit: %.2f",
                        rs.getString("Specialitate"),
                        rs.getDouble("VenituriGenerale"),
                        rs.getDouble("ComisionNegociat"),
                        rs.getDouble("SalariuCalculat"),
                        rs.getDouble("Profit"));
                resultBox.getChildren().add(new Label(result));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "A apărut o eroare la încărcarea profitului pe specialitate.");
            e.printStackTrace();
        }

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> returnToMainMenu());
        resultBox.getChildren().add(backButton);

        updateRoot(resultBox);
    }

    private void returnToMainMenu() {
        VBox mainMenu = createMainMenu(); // primaryStage reference no longer required
        updateRoot(mainMenu);
    }

    private void returnToDashboard() {
        // Use the stored primaryStage reference instead of passing it as a parameter
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        ExpertContabilDashboardScene dashboardScene = new ExpertContabilDashboardScene(primaryStage, connection);
        primaryStage.setScene(dashboardScene.getScene());
    }

    private void updateRoot(VBox newContent) {
        root.getChildren().clear();
        root.getChildren().add(newContent);
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