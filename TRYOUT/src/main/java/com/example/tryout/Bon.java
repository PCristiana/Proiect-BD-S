package com.example.tryout;

import javafx.beans.property.*;

public class Bon {
    private IntegerProperty idBon;
    private IntegerProperty idServiciu;
    private StringProperty dataProgramare;
    private StringProperty oraProgramare;
    private DoubleProperty pret;
    private IntegerProperty idClient;

    // Constructor
    public Bon(int idBon, int idServiciu, String dataProgramare, String oraProgramare, double pret, int idClient) {
        this.idBon = new SimpleIntegerProperty(idBon);
        this.idServiciu = new SimpleIntegerProperty(idServiciu);
        this.dataProgramare = new SimpleStringProperty(dataProgramare);
        this.oraProgramare = new SimpleStringProperty(oraProgramare);
        this.pret = new SimpleDoubleProperty(pret);
        this.idClient = new SimpleIntegerProperty(idClient);
    }

    // Getteri și setteri
    public int getIdBon() {
        return idBon.get();
    }

    public void setIdBon(int idBon) {
        this.idBon.set(idBon);
    }

    public IntegerProperty idBonProperty() {
        return idBon;
    }

    public int getIdServiciu() {
        return idServiciu.get();
    }

    public void setIdServiciu(int idServiciu) {
        this.idServiciu.set(idServiciu);
    }

    public IntegerProperty idServiciuProperty() {
        return idServiciu;
    }

    public String getDataProgramare() {
        return dataProgramare.get();
    }

    public void setDataProgramare(String dataProgramare) {
        this.dataProgramare.set(dataProgramare);
    }

    public StringProperty dataProgramareProperty() {
        return dataProgramare;
    }

    public String getOraProgramare() {
        return oraProgramare.get();
    }

    public void setOraProgramare(String oraProgramare) {
        this.oraProgramare.set(oraProgramare);
    }

    public StringProperty oraProgramareProperty() {
        return oraProgramare;
    }

    public double getPret() {
        return pret.get();
    }

    public void setPret(double pret) {
        this.pret.set(pret);
    }

    public DoubleProperty pretProperty() {
        return pret;
    }

    public int getIdClient() {
        return idClient.get();
    }

    public void setIdClient(int idClient) {
        this.idClient.set(idClient);
    }

    public IntegerProperty idClientProperty() {
        return idClient;
    }


}
