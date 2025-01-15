package com.example.tryout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class MyConnection {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/MedicalCenterBD";
            String username = "root";
            String password = "root";
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.err.println("Eroare la conectarea la baza de date: " + e.getMessage());
            return null;
        }
    }
}