package com.example.tryout;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MedicDashboardScene {
    private Scene scene;
    public String cnpMedic =Session.getLoggedUserCNP();

    public MedicDashboardScene(Stage primaryStage, Connection connection) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #87CEEB;");  // Background albastru

        // Titlul
        Label titleLabel = new Label("Programările mele");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.GRAY);

        // Tabelul pentru programări
        TableView<Appointment> appointmentsTable = new TableView<>();
        configureTableColumns(appointmentsTable);

        // Încarcă programările din baza de date
        loadAppointmentsData(connection, appointmentsTable);

        // Buton Adaugă Serviciu
        Button addServiceButton = new Button("Adaugă serviciu");
        addServiceButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");

        // Buton Șterge Serviciu
        Button deleteServiceButton = new Button("Șterge serviciu");
        deleteServiceButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #FF6347; -fx-text-fill: white;");

        // Form pentru adăugarea serviciilor
        TextField serviceNameField = new TextField();
        serviceNameField.setPromptText("Numele serviciului");

        TextField servicePriceField = new TextField();
        servicePriceField.setPromptText("Preț serviciu");

        TextField serviceDurationField = new TextField();
        serviceDurationField.setPromptText("Durata (minute)");

        addServiceButton.setOnAction(e -> {
            addService(connection, serviceNameField.getText(), servicePriceField.getText(), serviceDurationField.getText());
        });

        deleteServiceButton.setOnAction(e -> {
            deleteService(connection, serviceNameField.getText());
        });

        // Buton pentru modificarea datelor medicului
        Button modifyDataButton = new Button("Modifică datele personale");
        modifyDataButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");
        modifyDataButton.setOnAction(e -> openModifyDataWindow(primaryStage, connection));

        // Butonul Înapoi
        Button logoutButton = new Button("Logout");
        Session.setLoggedUserCNP("");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #FF6347; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(primaryStage);
            primaryStage.setScene(loginScene.getScene());
        });
        Button addReportButton = new Button("Adaugă Raport Medical");
        addReportButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");

        addReportButton.setOnAction(e -> openAddReportWindow(primaryStage, connection));

        // Adăugăm toate componentele în root
        root.getChildren().addAll(
                titleLabel, appointmentsTable,
                new Label("Adaugă/Șterge Serviciu"),
                serviceNameField, servicePriceField, serviceDurationField,
                addServiceButton, deleteServiceButton,modifyDataButton,addReportButton,
                logoutButton
        );

        this.scene = new Scene(root, 1000, 600);
    }

    public Scene getScene() {
        return this.scene;
    }

    // Metoda pentru deschiderea ferestrei de modificare a datelor medicului
    private void openModifyDataWindow(Stage primaryStage, Connection connection) {
        Stage modifyStage = new Stage();
        modifyStage.setTitle("Modifică datele personale");

        VBox modifyRoot = new VBox(20);
        modifyRoot.setPadding(new Insets(20));
        modifyRoot.setStyle("-fx-background-color: #87CEEB;");  // Fundal albastru similar cu pagina cu programările

        // Titlu
        Label titleLabel = new Label("Modifică datele personale");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));  // Font similar
        titleLabel.setTextFill(Color.GRAY);

        // Câmpuri pentru modificarea datelor
        TextField nameField = new TextField();
        nameField.setPromptText("Nume");
        nameField.setStyle("-fx-font-size: 14px;");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Prenume");
        surnameField.setStyle("-fx-font-size: 14px;");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact");
        contactField.setStyle("-fx-font-size: 14px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-font-size: 14px;");

        TextField addressField = new TextField();
        addressField.setPromptText("Adresă");
        addressField.setStyle("-fx-font-size: 14px;");

        TextField ibanField = new TextField();
        ibanField.setPromptText("IBAN");
        ibanField.setStyle("-fx-font-size: 14px;");

        // Încarcă datele medicului
        loadMedicData(connection, nameField, surnameField, contactField, emailField, addressField, ibanField);

        // Buton pentru salvarea modificărilor
        Button saveButton = new Button("Salvează modificările");
        saveButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");

        saveButton.setOnAction(e -> saveMedicData(connection, nameField.getText(), surnameField.getText(),
                contactField.getText(), emailField.getText(), addressField.getText(), ibanField.getText()));



        // Adăugăm toate componentele în root
        modifyRoot.getChildren().addAll(
                titleLabel, nameField, surnameField, contactField, emailField, addressField, ibanField, saveButton
        );

        Scene modifyScene = new Scene(modifyRoot, 400, 400);
        modifyStage.setScene(modifyScene);
        modifyStage.show();
    }

    // Metoda pentru încărcarea datelor medicului curent
    private void loadMedicData(Connection connection, TextField nameField, TextField surnameField,
                               TextField contactField, TextField emailField, TextField addressField, TextField ibanField) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        String query = "SELECT Nume, Prenume, Contact, Email, Adresa, IBAN FROM Utilizator WHERE CNP = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cnpMedic);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("Nume"));
                surnameField.setText(rs.getString("Prenume"));
                contactField.setText(rs.getString("Contact"));
                emailField.setText(rs.getString("Email"));
                addressField.setText(rs.getString("Adresa"));
                ibanField.setText(rs.getString("IBAN"));
            } else {
                System.out.println("Nu s-au găsit datele medicului.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea datelor medicului: " + e.getMessage());
        }
    }

    // Metoda pentru salvarea modificărilor în baza de date
    private void saveMedicData(Connection connection, String name, String surname, String contact,
                               String email, String address, String iban) {
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
            stmt.setString(7, cnpMedic);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Datele medicului au fost actualizate cu succes.");
            } else {
                System.out.println("Nu s-a reușit actualizarea datelor.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea datelor medicului: " + e.getMessage());
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

    private void loadAppointmentsData(Connection connection, TableView<Appointment> tableView) {
        //String cnpMedic = Session.getLoggedUserCNP();  // CNP-ul introdus de utilizator

        // Obținem id_medic pe baza CNP
        String queryMedic = "SELECT m.id_medic FROM Medic m JOIN Angajati a ON m.id_angajat = a.id_angajat WHERE a.CNP = ?";
        int medicId = -1;

        try (PreparedStatement stmt = connection.prepareStatement(queryMedic)) {
            stmt.setString(1, cnpMedic);  // Setăm CNP-ul în interogare
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                medicId = rs.getInt("id_medic");  // Obținem ID-ul medicului
            } else {
                System.out.println("Medicul nu a fost găsit!");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la autentificare medic: " + e.getMessage());
        }

        // Dacă medicul a fost găsit, obținem programările
        if (medicId != -1) {
            String queryProgramari = """
            SELECT 
                p.data_programare, 
                p.ora_programare, 
                s.nume_serviciu, 
                p.status
            FROM 
                Programari p
            JOIN 
                ServiciiMedicale s ON p.id_serviciu = s.id_serviciu
            WHERE 
                s.id_medic = ? 
            ORDER BY 
                p.data_programare, p.ora_programare
            """;

            try (PreparedStatement stmt = connection.prepareStatement(queryProgramari)) {
                stmt.setInt(1, medicId);  // Setăm ID-ul medicului în interogare
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Date dataProgramare = rs.getDate("data_programare");
                    Time oraProgramare = rs.getTime("ora_programare");
                    String numeServiciu = rs.getString("nume_serviciu");
                    String status = rs.getString("status");

                    // Creăm un obiect Appointment
                    Appointment appointment = new Appointment(dataProgramare.toString(), oraProgramare.toString(), numeServiciu, status);

                    // Adăugăm programarea în tabel
                    tableView.getItems().add(appointment);
                }
            } catch (SQLException e) {
                System.err.println("Eroare la obținerea programărilor: " + e.getMessage());
            }
        } else {
            System.out.println("Autentificarea a eșuat. CNP-ul introdus este incorect.");
        }
    }

    // Metoda pentru adăugarea unui serviciu
    private void addService(Connection connection, String serviceName, String servicePrice, String serviceDuration) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        // Validăm intrările
        if (serviceName.isEmpty() || servicePrice.isEmpty() || serviceDuration.isEmpty()) {
            System.out.println("Toate câmpurile trebuie completate.");
            return;
        }

        try {
            // Validăm dacă prețul și durata sunt valide
            BigDecimal price = new BigDecimal(servicePrice);
            int duration = Integer.parseInt(serviceDuration);

            // Verificăm dacă durata este validă (nu poate fi mai mică decât 1 minut)
            if (duration <= 0) {
                System.out.println("Durata trebuie să fie mai mare decât 0 minute.");
                return;
            }

            // Preluăm ID-ul medicului
            int medicId = getMedicId(connection);
            if (medicId == -1) {
                System.out.println("ID-ul medicului nu a putut fi obținut.");
                return;
            }

            // Interogare SQL pentru adăugarea serviciului
            String query = "INSERT INTO ServiciiMedicale (nume_serviciu, pret, durata_minute, id_medic) VALUES (?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, serviceName);
                stmt.setBigDecimal(2, price);
                stmt.setInt(3, duration);
                stmt.setInt(4, medicId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Serviciu adăugat cu succes!");
                } else {
                    System.out.println("Nu s-a reușit adăugarea serviciului.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Prețul și durata trebuie să fie valori numerice valide.");
        } catch (SQLException e) {
            System.err.println("Eroare la adăugarea serviciului: " + e.getMessage());
        }
    }

    // Metoda pentru ștergerea unui serviciu
    private void deleteService(Connection connection, String serviceName) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        String query = "DELETE FROM ServiciiMedicale WHERE nume_serviciu = ? AND id_medic = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, serviceName);
            stmt.setInt(2, getMedicId(connection));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Serviciu șters cu succes!");
            } else {
                System.out.println("Nu s-a găsit serviciul pentru ștergere.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea serviciului: " + e.getMessage());
        }
    }


    // Obține ID-ul medicului din baza de date pe baza CNP-ului din sesiune
    private int getMedicId(Connection connection) {

        if (cnpMedic == null || cnpMedic.isEmpty()) {
            System.out.println("CNP-ul medicului este invalid sau nu este setat.");
            return -1;  // Returnăm -1 pentru a semnala o eroare
        }

        // Interogare SQL pentru obținerea ID-ului medicului pe baza CNP-ului
        String query = "SELECT id_medic FROM Medic WHERE id_angajat = (SELECT id_angajat FROM Angajati WHERE CNP = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cnpMedic);  // Setăm CNP-ul în interogare
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_medic");  // Dacă se găsește, returnăm ID-ul medicului
            } else {
                System.out.println("Nu s-a găsit medicul cu CNP-ul: " + cnpMedic);
                return -1;  // Dacă nu găsim medicul, returnăm -1
            }
        } catch (SQLException e) {
            System.err.println("Eroare la obținerea ID-ului medicului: " + e.getMessage());
            return -1;  // Returnăm -1 în caz de eroare
        }
    }
    private void openAddReportWindow(Stage primaryStage, Connection connection) {
        Stage reportStage = new Stage();
        reportStage.setTitle("Completează Raport Medical");

        VBox reportRoot = new VBox(20);
        reportRoot.setPadding(new Insets(20));
        reportRoot.setStyle("-fx-background-color: #87CEEB;");

        // Câmpuri pentru completarea raportului
        TextField pacientNumeField = new TextField();
        pacientNumeField.setPromptText("Nume pacient");
        TextField pacientPrenumeField = new TextField();
        pacientPrenumeField.setPromptText("Prenume pacient");

        TextField medicNumeField = new TextField();
        medicNumeField.setPromptText("Nume medic");
        TextField medicPrenumeField = new TextField();
        medicPrenumeField.setPromptText("Prenume medic");

        DatePicker dataConsultatiePicker = new DatePicker();
        dataConsultatiePicker.setPromptText("Data consultației");

        TextArea istoriculField = new TextArea();
        istoriculField.setPromptText("Istoric medical");

        TextArea simptomeField = new TextArea();
        simptomeField.setPromptText("Simptome");

        TextArea investigatiiField = new TextArea();
        investigatiiField.setPromptText("Investigații");

        TextArea diagnosticField = new TextArea();
        diagnosticField.setPromptText("Diagnostic");

        TextArea recomandariField = new TextArea();
        recomandariField.setPromptText("Recomandări");

        // ComboBox pentru selectarea pacientului
        ComboBox<Clienti> pacientComboBox = new ComboBox<>();
        loadPacienti(connection, pacientComboBox); // Încarcă pacienții în ComboBox

        // Buton pentru a salva raportul
        Button saveButton = new Button("Salvează raportul");
        saveButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");

        // Adăugăm acțiunea butonului pentru a salva raportul atunci când este apăsat
        saveButton.setOnAction(e -> {
            Clienti pacientSelectat = pacientComboBox.getValue(); // Obținem pacientul selectat
            if (pacientSelectat != null && !medicNumeField.getText().isEmpty() && !medicPrenumeField.getText().isEmpty()) {
                saveReport(connection, pacientSelectat, medicNumeField.getText(), medicPrenumeField.getText(),
                        "", "", dataConsultatiePicker.getValue(),
                        istoriculField.getText(), simptomeField.getText(), investigatiiField.getText(),
                        diagnosticField.getText(), recomandariField.getText());
            } else {
                System.out.println("Te rog să selectezi un pacient și să completezi câmpurile medicului!");
            }
        });
        // Buton pentru validarea raportului
        Button validateButton = new Button("Validare raport");
        validateButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #32CD32; -fx-text-fill: white;");

        // Adăugăm acțiunea butonului pentru a valida raportul atunci când este apăsat
        validateButton.setOnAction(e -> {
            Clienti pacientSelectat = pacientComboBox.getValue(); // Obținem pacientul selectat
            if (pacientSelectat != null) {
                // Apelează metoda de validare
                validateReport(connection, pacientSelectat);

                // Dezactivează câmpurile de input
                pacientNumeField.setDisable(true);
                pacientPrenumeField.setDisable(true);
                medicNumeField.setDisable(true);
                medicPrenumeField.setDisable(true);
                dataConsultatiePicker.setDisable(true);
                istoriculField.setDisable(true);
                simptomeField.setDisable(true);
                investigatiiField.setDisable(true);
                diagnosticField.setDisable(true);
                recomandariField.setDisable(true);
                pacientComboBox.setDisable(true);
                saveButton.setDisable(true);  // Dezactivează și butonul de salvare pentru a preveni modificările suplimentare
            } else {
                System.out.println("Te rog să selectezi un pacient înainte de a valida raportul.");
            }
        });
        // Adăugăm componentele în layout
        reportRoot.getChildren().addAll(
                new Label("Selectează medic"), pacientComboBox,
                new Label("Nume pacient"), pacientNumeField,
                new Label("Prenume pacient"), pacientPrenumeField,
                new Label("Nume medic"), medicNumeField,
                new Label("Prenume medic"), medicPrenumeField,
                new Label("Data consultației"), dataConsultatiePicker,
                new Label("Istoric medical"), istoriculField,
                new Label("Simptome"), simptomeField,
                new Label("Investigații"), investigatiiField,
                new Label("Diagnostic"), diagnosticField,
                new Label("Recomandări"), recomandariField,
                saveButton
        );
        // Adăugăm butonul de validare la layout
        reportRoot.getChildren().add(validateButton);

        // Creăm un ScrollPane care conține root-ul VBox
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(reportRoot);  // Setăm VBox-ul ca și conținut
        scrollPane.setFitToHeight(true);    // Ajustăm scroll-ul pe înălțime
        scrollPane.setFitToWidth(true);     // Ajustăm scroll-ul pe lățime

        // Creăm scena și setăm ScrollPane-ul ca root
        Scene reportScene = new Scene(scrollPane, 400, 600);
        reportStage.setScene(reportScene);
        reportStage.show();
    }
    private void loadPacienti(Connection connection, ComboBox<Clienti> pacientComboBox) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        String query = "SELECT id_client, nume_client, prenume_client FROM Clienti";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            ObservableList<Clienti> pacientiList = FXCollections.observableArrayList();
            while (rs.next()) {
                int idClient = rs.getInt("id_client");
                String numeClient = rs.getString("nume_client");
                String prenumeClient = rs.getString("prenume_client");
                Clienti pacient = new Clienti(idClient, numeClient, prenumeClient);
                pacientiList.add(pacient);
            }
            pacientComboBox.setItems(pacientiList);
            pacientComboBox.setCellFactory(param -> new ListCell<Clienti>() {
                @Override
                protected void updateItem(Clienti item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getNume() + " " + item.getPrenume());
                    }
                }
            });
            pacientComboBox.setButtonCell(new ListCell<Clienti>() {
                @Override
                protected void updateItem(Clienti item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getNume() + " " + item.getPrenume());
                    }
                }
            });
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea pacienților: " + e.getMessage());
        }
    }

    private void loadMedicalHistory(Connection connection, int pacientId, TextField pacientNumeField, TextField pacientPrenumeField) {
        String query = "SELECT r.istoric FROM RaportMedical r WHERE r.id_client = ? ORDER BY r.data_consultatie DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, pacientId);
            ResultSet rs = stmt.executeQuery();

            // Dacă găsim rapoarte medicale, le vom afișa
            StringBuilder history = new StringBuilder();
            while (rs.next()) {
                history.append(rs.getString("istoric")).append("\n\n");
            }

            // Afișăm istoricul pacientului în câmpul corespunzător
            pacientNumeField.setText("Istoric Medical:\n" + history.toString());
        } catch (SQLException e) {
            System.err.println("Eroare la încărcarea istoricului medical: " + e.getMessage());
        }
    }
    private void saveReport(Connection connection, Clienti pacient, String medicNume, String medicPrenume, String medicRecomandator,
                            String asistentNume, LocalDate dataConsultatie, String istoric, String simptome, String investigatii,
                            String diagnostic, String recomandari) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }

        // Obținem ID-ul medicului curent
        int medicId = getMedicId(connection);

        // Verificăm dacă asistentul este valid
        Integer asistentId = null;
        if (!asistentNume.isEmpty()) {
            asistentId = getAsistentId(connection, asistentNume);
        }

        String query = "INSERT INTO RaportMedical (id_client, id_medic, id_asistent, data_consultatie, istoric, simptome, investigatii, diagnostic, recomandari) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, pacient.getIdClient());    // ID-ul pacientului
            stmt.setInt(2, medicId);                  // ID-ul medicului curent
            if (asistentId != null) {
                stmt.setInt(3, asistentId);           // ID-ul asistentului (dacă există)
            } else {
                stmt.setNull(3, Types.INTEGER);       // Dacă nu există, setăm null
            }
            stmt.setDate(4, Date.valueOf(dataConsultatie)); // Data consultației
            stmt.setString(5, istoric);               // Istoricul medical
            stmt.setString(6, simptome);              // Simptomele
            stmt.setString(7, investigatii);          // Investigațiile
            stmt.setString(8, diagnostic);            // Diagnosticul
            stmt.setString(9, recomandari);          // Recomandările

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Raportul a fost salvat cu succes.");
            } else {
                System.out.println("Nu s-a reușit salvarea raportului.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea raportului: " + e.getMessage());
        }
    }

    private int getServiciuIdByMedic(Connection connection, int medicId) {
        String query = "SELECT id_serviciu FROM ServiciiMedicale WHERE id_medic = ? LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, medicId);  // Setează ID-ul medicului
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_serviciu");  // Returnează ID-ul serviciului asociat medicului
            } else {
                System.out.println("Nu s-a găsit un serviciu asociat medicului.");
                return -1;  // Returnează -1 dacă nu există niciun serviciu asociat
            }
        } catch (SQLException e) {
            System.err.println("Eroare la obținerea ID-ului serviciului: " + e.getMessage());
            return -1;
        }
    }
    private void validateReport(Connection connection, Clienti pacient) {
        if (connection == null || !isConnectionValid(connection)) {
            connection = MyConnection.getConnection();
            if (connection == null || !isConnectionValid(connection)) {
                System.err.println("Nu s-a putut stabili conexiunea la baza de date.");
                return;
            }
        }
        String query = "UPDATE RaportMedical SET validat = TRUE, data_validare = NOW() WHERE id_client = ? AND validat = FALSE";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, pacient.getIdClient());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Raportul a fost validat.");
            } else {
                System.out.println("Raportul nu poate fi validat.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare la validarea raportului: " + e.getMessage());
        }
    }
    private int getAsistentId(Connection connection, String asistentNume) {
        String query = "SELECT id_asistent FROM AsistentMedical WHERE nume = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, asistentNume); // Setează numele asistentului în interogare
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_asistent"); // Returnează ID-ul asistentului găsit
            } else {
                System.out.println("Asistentul nu a fost găsit.");
                return -1; // Returnează -1 dacă asistentul nu este găsit
            }
        } catch (SQLException e) {
            System.err.println("Eroare la obținerea ID-ului asistentului: " + e.getMessage());
            return -1;
        }
    }
    private int getMedicIdByName(Connection connection, String medicName) {
        String query = "SELECT id_medic FROM Medic WHERE CONCAT(nume, ' ', prenume) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, medicName); // Setează numele complet al medicului recomandator în interogare
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_medic"); // Returnează ID-ul medicului recomandator
            } else {
                System.out.println("Medicul recomandator nu a fost găsit.");
                return -1; // Returnează -1 dacă medicul recomandator nu este găsit
            }
        } catch (SQLException e) {
            System.err.println("Eroare la obținerea ID-ului medicului recomandator: " + e.getMessage());
            return -1;
        }
    }
    private boolean isConnectionValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

}
