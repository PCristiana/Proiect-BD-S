package com.example.tryout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UtilizatorDAO {

    private Connection connection;

    // Constructor pentru a primi conexiunea la baza de date
    public UtilizatorDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodă pentru adăugarea unui utilizator în baza de date
    public boolean addUtilizator(Utilizator utilizator) {
        String sql = "INSERT INTO Utilizator (CNP, Nume, Prenume, DataAngajare, ContractNumar, Contact, Email, Adresa, IBAN, Pozitie, id_unitate, tip_utilizator) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, utilizator.getCnp());
            statement.setString(2, utilizator.getNume());
            statement.setString(3, utilizator.getPrenume());
            statement.setDate(4, utilizator.getDataAngajare());
            statement.setInt(5, utilizator.getContractNumar());
            statement.setString(6, utilizator.getContact());
            statement.setString(7, utilizator.getEmail());
            statement.setString(8, utilizator.getAdresa());
            statement.setString(9, utilizator.getIban());
            statement.setString(10, utilizator.getPozitie());
            statement.setInt(11, utilizator.getIdUnitate());
            statement.setString(12, utilizator.getTipUtilizator());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
