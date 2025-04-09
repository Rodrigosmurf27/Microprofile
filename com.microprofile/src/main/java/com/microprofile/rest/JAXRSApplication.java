package com.microprofile.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@ApplicationPath("/api")
@OpenAPIDefinition(
        info = @Info(
                title = "API de Productos",
                version = "1.0.0",
                description = "API RESTful para la gestión de productos",
                contact = @Contact(
                        name = "Soporte MicroProfile",
                        email = "soporte@microprofile.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class JAXRSApplication extends Application {
    // No es necesario implementar otros métodos
}