package com.example.tryout;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Patient {
    private final StringProperty name;
    private final StringProperty cnp;

    public Patient(String name, String cnp) {
        this.name = new SimpleStringProperty(name);
        this.cnp = new SimpleStringProperty(cnp);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty cnpProperty() {
        return cnp;
    }

    public String getCnp() {
        return cnp.get();
    }

    public void setCnp(String cnp) {
        this.cnp.set(cnp);
    }
}
