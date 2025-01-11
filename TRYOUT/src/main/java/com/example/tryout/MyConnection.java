package com.example.tryout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null || !isConnectionValid()) {
            try {
                String url = "jdbc:mysql://localhost:3306/numele_bazei_de_date?autoReconnect=true&useSSL=false";
                String user = "utilizator";
                String password = "parola";
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Conexiune la baza de date reușită.");
            } catch (SQLException e) {
                System.err.println("Eroare la conectarea la baza de date: " + e.getMessage());
            }
        }
        return connection;
    }

    private static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
