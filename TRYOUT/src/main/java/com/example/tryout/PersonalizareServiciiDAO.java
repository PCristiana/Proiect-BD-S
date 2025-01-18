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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonalizareServiciiDAO {
    private Connection connection;

    public PersonalizareServiciiDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodă pentru obținerea serviciilor personalizate pentru un medic
    public List<PersonalizareServicii> getServiciiPersonalizate(int idMedic) throws SQLException {
        String query = """
            SELECT ps.id_personalizare, ps.id_serviciu, ps.pret_personalizat, ps.durata_personalizata
            FROM PersonalizareServicii ps
            WHERE ps.id_medic = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idMedic);
            ResultSet rs = stmt.executeQuery();
            List<PersonalizareServicii> serviciiPersonalizate = new ArrayList<>();
            while (rs.next()) {
                serviciiPersonalizate.add(new PersonalizareServicii(
                        rs.getInt("id_personalizare"),
                        idMedic,
                        rs.getInt("id_serviciu"),
                        rs.getBigDecimal("pret_personalizat"),
                        rs.getInt("durata_personalizata")
                ));
            }
            return serviciiPersonalizate;
        }
    }

    // Metodă pentru salvarea sau actualizarea unui serviciu personalizat
    public void saveOrUpdate(PersonalizareServicii personalizare) throws SQLException {
        String query = """
            INSERT INTO PersonalizareServicii (id_personalizare, id_medic, id_serviciu, pret_personalizat, durata_personalizata)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            pret_personalizat = VALUES(pret_personalizat),
            durata_personalizata = VALUES(durata_personalizata)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, personalizare.getIdPersonalizare());
            stmt.setInt(2, personalizare.getIdMedic());
            stmt.setInt(3, personalizare.getIdServiciu());
            stmt.setBigDecimal(4, personalizare.getPretPersonalizat());
            stmt.setInt(5, personalizare.getDurataPersonalizata());
            stmt.executeUpdate();
        }
    }

    // Metodă pentru ștergerea unui serviciu personalizat
    public void deletePersonalizare(int idPersonalizare) throws SQLException {
        String query = "DELETE FROM PersonalizareServicii WHERE id_personalizare = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idPersonalizare);
            stmt.executeUpdate();
        }
    }

    // Metodă pentru obținerea unei personalizări după ID
    public PersonalizareServicii getPersonalizareById(int idPersonalizare) throws SQLException {
        String query = """
            SELECT ps.id_personalizare, ps.id_medic, ps.id_serviciu, ps.pret_personalizat, ps.durata_personalizata
            FROM PersonalizareServicii ps
            WHERE ps.id_personalizare = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idPersonalizare);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PersonalizareServicii(
                        rs.getInt("id_personalizare"),
                        rs.getInt("id_medic"),
                        rs.getInt("id_serviciu"),
                        rs.getBigDecimal("pret_personalizat"),
                        rs.getInt("durata_personalizata")
                );
            }
        }
        return null;
    }
}
