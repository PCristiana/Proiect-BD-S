package com.example.tryout;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicVenituri {
    private String numeMedic;
    private double venituri;
    private String specialitate;

    public MedicVenituri(String numeMedic, double venituri, String specialitate) {
        this.numeMedic = numeMedic;
        this.venituri = venituri;
        this.specialitate = specialitate;
    }

    public String getNumeMedic() {
        return numeMedic;
    }

    public void setNumeMedic(String numeMedic) {
        this.numeMedic = numeMedic;
    }

    public double getVenituri() {
        return venituri;
    }

    public void setVenituri(double venituri) {
        this.venituri = venituri;
    }

    public String getSpecialitate() {
        return specialitate;
    }

    public void setSpecialitate(String specialitate) {
        this.specialitate = specialitate;
    }
}
