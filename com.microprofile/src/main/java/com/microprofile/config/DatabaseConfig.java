package com.microprofile.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());

    @ConfigProperty(name = "database.url")
    private String url;

    @ConfigProperty(name = "database.username")
    private String username;

    @ConfigProperty(name = "database.password")
    private String password;

    @Produces
    public Connection createConnection() {
        Connection conn = null;
        try {
            LOGGER.info("Intentando conectar a la base de datos con URL: " + url);
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            LOGGER.info("Conexión a la base de datos establecida exitosamente");
            return conn;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el driver JDBC: " + e.getMessage(), e);
            throw new RuntimeException("Error al cargar el driver JDBC", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la base de datos: " + e.getMessage(), e);
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }
}