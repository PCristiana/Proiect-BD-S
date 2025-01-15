package com.example.tryout;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// Clasa pentru reprezentarea unui utilizator
public class User {
    private final String cnp;
    private final int oreLucrate;
    private final double salariuNegociat;
    private final int idUnitate;
    private final String tipUtilizator;

    public User(String cnp, int oreLucrate, double salariuNegociat, int idUnitate, String tipUtilizator) {

        this.cnp = cnp;
        this.oreLucrate = oreLucrate;
        this.salariuNegociat = salariuNegociat;
        this.idUnitate = idUnitate;
        this.tipUtilizator = tipUtilizator;
    }

    public String getCnp() {
        return cnp;
    }

    public int getOreLucrate() {
        return oreLucrate;
    }

    public double getSalariuNegociat() {
        return salariuNegociat;
    }

    public int getIdUnitate() {
        return idUnitate;
    }

    public String getTipUtilizator() {
        return tipUtilizator;
    }
}

