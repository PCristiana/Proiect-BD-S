package com.example.tryout;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class Utilizator {
    private String cnp;
    private String nume;
    private String prenume;
    private Date dataAngajare;
    private int contractNumar;
    private String contact;
    private String email;
    private String adresa;
    private String iban;
    private String pozitie;
    private int idUnitate;
    private String tipUtilizator;

    // Constructor gol
    public Utilizator() {}

    // Constructor complet
    public Utilizator(String cnp, String nume, String prenume, Date dataAngajare, int contractNumar,
                      String contact, String email, String adresa, String iban, String pozitie,
                      int idUnitate, String tipUtilizator) {
        this.cnp = cnp;
        this.nume = nume;
        this.prenume = prenume;
        this.dataAngajare = dataAngajare;
        this.contractNumar = contractNumar;
        this.contact = contact;
        this.email = email;
        this.adresa = adresa;
        this.iban = iban;
        this.pozitie = pozitie;
        this.idUnitate = idUnitate;
        this.tipUtilizator = tipUtilizator;
    }

    // Getters și Setters
    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public Date getDataAngajare() { return dataAngajare; }
    public void setDataAngajare(Date dataAngajare) { this.dataAngajare = dataAngajare; }
    public int getContractNumar() { return contractNumar; }
    public void setContractNumar(int contractNumar) { this.contractNumar = contractNumar; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public String getPozitie() { return pozitie; }
    public void setPozitie(String pozitie) { this.pozitie = pozitie; }
    public int getIdUnitate() { return idUnitate; }
    public void setIdUnitate(int idUnitate) { this.idUnitate = idUnitate; }
    public String getTipUtilizator() { return tipUtilizator; }
    public void setTipUtilizator(String tipUtilizator) { this.tipUtilizator = tipUtilizator; }

    // Metodă pentru a adăuga utilizatorul în baza de date
    public boolean addUtilizator() {
        String query = "INSERT INTO Utilizator (CNP, Nume, Prenume, DataAngajare, ContractNumar, Contact, Email, Adresa, IBAN, Pozitie, id_unitate, tip_utilizator) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = MyConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, this.cnp);
            preparedStatement.setString(2, this.nume);
            preparedStatement.setString(3, this.prenume);
            preparedStatement.setDate(4, this.dataAngajare);
            preparedStatement.setInt(5, this.contractNumar);
            preparedStatement.setString(6, this.contact);
            preparedStatement.setString(7, this.email);
            preparedStatement.setString(8, this.adresa);
            preparedStatement.setString(9, this.iban);
            preparedStatement.setString(10, this.pozitie);
            preparedStatement.setInt(11, this.idUnitate);
            preparedStatement.setString(12, this.tipUtilizator);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Eroare la înregistrare: " + e.getMessage());
            return false;
        }
    }
}
