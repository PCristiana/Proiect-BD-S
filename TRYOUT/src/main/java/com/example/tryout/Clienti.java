package com.example.tryout;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Clienti {
    private String cnp;
    private String nume;
    private String prenume;
    private String contact;
    private String email;
    private String adresa;

    // Constructor
    public Clienti(String cnp, String nume, String prenume, String contact, String email, String adresa) {
        this.cnp = cnp;
        this.nume = nume;
        this.prenume = prenume;
        this.contact = contact;
        this.email = email;
        this.adresa = adresa;
    }

    public Clienti() {

    }

    // Getteri și setteri
    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    // Metodă pentru salvarea clientului în baza de date
    public boolean addClient() {
        String sql = "INSERT INTO Clienti (CNP, nume_client, prenume_client, contact, email, adresa) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = MyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnp);
            stmt.setString(2, nume);
            stmt.setString(3, prenume);
            stmt.setString(4, contact);
            stmt.setString(5, email);
            stmt.setString(6, adresa);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Eroare la salvarea clientului: " + e.getMessage());
            return false;
        }
    }
}
