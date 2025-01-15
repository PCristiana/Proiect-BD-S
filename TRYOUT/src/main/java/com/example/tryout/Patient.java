package com.example.tryout;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Patient {
    private final StringProperty name;
    private final StringProperty cnp;
    private final StringProperty medicalHistory;

    public Patient(String name, String cnp, String medicalHistory) {
        this.name = new SimpleStringProperty(name);
        this.cnp = new SimpleStringProperty(cnp);
        this.medicalHistory = new SimpleStringProperty(medicalHistory);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty cnpProperty() {
        return cnp;
    }

    public StringProperty medicalHistoryProperty() {
        return medicalHistory;
    }

    public String getName() {
        return name.get();
    }

    public String getCnp() {
        return cnp.get();
    }

    public String getMedicalHistory() {
        return medicalHistory.get();
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory.set(medicalHistory);
    }
}