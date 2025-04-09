package com.microprofile.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class DatabaseConnectionProducer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionProducer.class.getName());

    // URLs para probar conexión (si Azure falla)
    private static final String AZURE_URL = "jdbc:mysql://microprofile.database.windows.net:1433/microprofile_demo?useSSL=true&requireSSL=true";
    private static final String BACKUP_URL = "jdbc:mysql://microprofile.database.windows.net:3306/microprofile_demo?useSSL=true";

    private final String username = "adminsql";
    private final String password = "ferr@riGT1";

    @Produces
    @Alternative
    public Connection produceConnection(InjectionPoint injectionPoint) {
        LOGGER.info("Intentando producir conexión a la base de datos...");

        // Primer intento - URL Azure estándar con puerto 1433
        try {
            LOGGER.info("Intentando conectar con URL Azure en puerto 1433: " + AZURE_URL);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(AZURE_URL, username, password);
            LOGGER.info("Conexión exitosa usando URL Azure puerto 1433");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.WARNING, "Falló la conexión con URL Azure en puerto 1433: " + e.getMessage());
        }

        // Segundo intento - URL de respaldo con puerto 3306 (puerto MySQL estándar)
        try {
            LOGGER.info("Intentando conectar con URL de respaldo en puerto 3306: " + BACKUP_URL);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(BACKUP_URL, username, password);
            LOGGER.info("Conexión exitosa usando URL de respaldo puerto 3306");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "También falló la conexión de respaldo: " + e.getMessage());
        }

        // Si todo falla, crea una conexión ficticia para pruebas
        LOGGER.severe("No se pudo establecer conexión con la base de datos. Usando conexión de prueba.");
        try {
            return getMockConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error fatal al intentar crear conexión de prueba", e);
        }
    }

    // Método para simular una conexión para pruebas en caso de fallo total
    private Connection getMockConnection() throws SQLException {
        // Intenta conectar a una base de datos H2 en memoria como último recurso
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo crear una conexión de prueba", e);
        }
    }
}