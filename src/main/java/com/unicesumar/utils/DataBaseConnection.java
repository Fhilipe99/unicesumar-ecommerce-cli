package com.unicesumar.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static final String URL = "jdbc:sqlite:database.sqlite"; // ou o nome do seu banco

    // Método utilitário que retorna a conexão
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
