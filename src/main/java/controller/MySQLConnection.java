package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class MySQLConnection {
    private final Dotenv dotenv = Dotenv.load();
    private String URL = dotenv.get("BD_URL");
    private String USER = dotenv.get("BD_USER");
    private String PASSWORD = dotenv.get("BD_PASSWORD");
    protected Connection db;
    
    public MySQLConnection() {
        try {
            db = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
