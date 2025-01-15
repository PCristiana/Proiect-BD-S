package com.example.tryout;

import java.math.BigDecimal;

public class PersonalizareServicii {
    private int idPersonalizare;
    private int idMedic;
    private int idServiciu;
    private BigDecimal pretPersonalizat;
    private int durataPersonalizata;

    // Constructor complet
    public PersonalizareServicii(int idPersonalizare, int idMedic, int idServiciu, BigDecimal pretPersonalizat, int durataPersonalizata) {
        this.idPersonalizare = idPersonalizare;
        this.idMedic = idMedic;
        this.idServiciu = idServiciu;
        this.pretPersonalizat = pretPersonalizat;
        this.durataPersonalizata = durataPersonalizata;
    }

    // Constructor gol
    public PersonalizareServicii() {}

    // Getteri și Setteri
    public int getIdPersonalizare() {
        return idPersonalizare;
    }

    public void setIdPersonalizare(int idPersonalizare) {
        this.idPersonalizare = idPersonalizare;
    }

    public int getIdMedic() {
        return idMedic;
    }

    public void setIdMedic(int idMedic) {
        this.idMedic = idMedic;
    }

    public int getIdServiciu() {
        return idServiciu;
    }

    public void setIdServiciu(int idServiciu) {
        this.idServiciu = idServiciu;
    }

    public BigDecimal getPretPersonalizat() {
        return pretPersonalizat;
    }

    public void setPretPersonalizat(BigDecimal pretPersonalizat) {
        this.pretPersonalizat = pretPersonalizat;
    }

    public int getDurataPersonalizata() {
        return durataPersonalizata;
    }

    public void setDurataPersonalizata(int durataPersonalizata) {
        this.durataPersonalizata = durataPersonalizata;
    }

    // Metoda toString
    @Override
    public String toString() {
        return "PersonalizareServicii{" +
                "idPersonalizare=" + idPersonalizare +
                ", idMedic=" + idMedic +
                ", idServiciu=" + idServiciu +
                ", pretPersonalizat=" + pretPersonalizat +
                ", durataPersonalizata=" + durataPersonalizata +
                '}';
    }
}
