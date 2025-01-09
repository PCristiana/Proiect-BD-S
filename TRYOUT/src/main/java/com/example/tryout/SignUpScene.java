package com.example.tryout;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;

public class SignUpScene {
    private Scene scene;

    public SignUpScene(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        // Setează fundalul la albastru deschis
        root.setStyle("-fx-background-color: #ADD8E6;");

        // Selector pentru tipul de înregistrare
        ComboBox<String> tipInregistrareCombo = new ComboBox<>();
        tipInregistrareCombo.getItems().addAll("Angajat", "Client");
        tipInregistrareCombo.setPromptText("Selectați tipul de înregistrare");

        // Câmpuri comune
        TextField cnpField = new TextField();
        cnpField.setPromptText("CNP");

        TextField numeField = new TextField();
        numeField.setPromptText("Nume");

        TextField prenumeField = new TextField();
        prenumeField.setPromptText("Prenume");

        // Câmpuri pentru utilizator
        DatePicker dataAngajarePicker = new DatePicker();
        TextField contractNumarField = new TextField();
        contractNumarField.setPromptText("Număr contract");
        TextField contactField = new TextField();
        contactField.setPromptText("Contact");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField adresaField = new TextField();
        adresaField.setPromptText("Adresă");
        TextField ibanField = new TextField();
        ibanField.setPromptText("IBAN");
        TextField pozitieField = new TextField();
        pozitieField.setPromptText("Poziție");
        TextField idUnitateField = new TextField();
        idUnitateField.setPromptText("ID Unitate");
        ComboBox<String> tipUtilizatorCombo = new ComboBox<>();
        tipUtilizatorCombo.getItems().addAll("admin", "super-admin", "angajat");
        tipUtilizatorCombo.setPromptText("Tip utilizator");

        // Câmpuri pentru client
        TextField contactClientField = new TextField();
        contactClientField.setPromptText("Contact Client");
        TextField emailClientField = new TextField();
        emailClientField.setPromptText("Email Client");
        TextField adresaClientField = new TextField();
        adresaClientField.setPromptText("Adresă Client");

        // Butoane
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();
        Button backButton = new Button("Back");

        // Afișarea câmpurilor în funcție de tipul selectat
        tipInregistrareCombo.setOnAction(e -> {
            root.getChildren().clear();
            root.getChildren().add(tipInregistrareCombo);

            if ("Angajat".equals(tipInregistrareCombo.getValue())) {
                root.getChildren().addAll(cnpField, numeField, prenumeField, dataAngajarePicker, contractNumarField,
                        contactField, emailField, adresaField, ibanField, pozitieField, idUnitateField,
                        tipUtilizatorCombo, registerButton, messageLabel, backButton);
            } else if ("Client".equals(tipInregistrareCombo.getValue())) {
                root.getChildren().addAll(cnpField, numeField, prenumeField, contactClientField, emailClientField,
                        adresaClientField, registerButton, messageLabel, backButton);
            }
        });

        // Logica pentru înregistrare
        registerButton.setOnAction(e -> {
            if ("Angajat".equals(tipInregistrareCombo.getValue())) {
                // Creare utilizator
                Utilizator utilizator = new Utilizator();
                utilizator.setCnp(cnpField.getText());
                utilizator.setNume(numeField.getText());
                utilizator.setPrenume(prenumeField.getText());
                utilizator.setDataAngajare(Date.valueOf(dataAngajarePicker.getValue()));
                utilizator.setContractNumar(Integer.parseInt(contractNumarField.getText()));
                utilizator.setContact(contactField.getText());
                utilizator.setEmail(emailField.getText());
                utilizator.setAdresa(adresaField.getText());
                utilizator.setIban(ibanField.getText());
                utilizator.setPozitie(pozitieField.getText());
                utilizator.setIdUnitate(Integer.parseInt(idUnitateField.getText()));
                utilizator.setTipUtilizator(tipUtilizatorCombo.getValue());

                if (utilizator.addUtilizator()) {
                    messageLabel.setText("Angajat înregistrat cu succes!");
                } else {
                    messageLabel.setText("Eroare la înregistrare utilizator.");
                }
            } else if ("Client".equals(tipInregistrareCombo.getValue())) {

                Clienti client = new Clienti();
                client.setCnp(cnpField.getText());
                client.setNume(numeField.getText());
                client.setPrenume(prenumeField.getText());
                client.setContact(contactClientField.getText());
                client.setEmail(emailClientField.getText());
                client.setAdresa(adresaClientField.getText());

                if (client.addClient()) {
                    messageLabel.setText("Client înregistrat cu succes!");
                } else {
                    messageLabel.setText("Eroare la înregistrare client.");
                }
            }
        });

        backButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });

        // Afișare inițială
        root.getChildren().add(tipInregistrareCombo);

        this.scene = new Scene(root, 400, 600);
    }

    public Scene getScene() {
        return this.scene;
    }
}
