package com.microprofile.util;

import java.sql.Connection;
import java.sql.SQLException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    @Inject
    private Connection connection;

    @Override
    public HealthCheckResponse call() {
        try {
            if (connection != null && !connection.isClosed()) {
                return HealthCheckResponse.named("database")
                        .up()
                        .withData("conexion", "activa")
                        .build();
            } else {
                return HealthCheckResponse.named("database")
                        .down()
                        .withData("error", "Conexión a base de datos cerrada o nula")
                        .build();
            }
        } catch (SQLException e) {
            return HealthCheckResponse.named("database")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}