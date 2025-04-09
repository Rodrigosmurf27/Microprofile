package com.microprofile.rest;

import com.microprofile.data.Producto;
import com.microprofile.service.ProductoService;
import java.sql.SQLException;
import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/productos")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "API de Productos", description = "Operaciones CRUD para productos")
public class ProductoResource {

    @Inject
    private ProductoService productoService;

    @GET
    @Operation(summary = "Listar todos los productos", description = "Retorna una lista con todos los productos disponibles")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response listarProductos() {
        try {
            List<Producto> productos = productoService.listarProductos();
            return Response.ok(productos).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al obtener los productos: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener un producto por ID", description = "Retorna un producto específico basado en su ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Producto encontrado"),
            @APIResponse(responseCode = "404", description = "Producto no encontrado"),
            @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response obtenerProducto(@PathParam("id") int id) {
        try {
            return productoService.obtenerProducto(id)
                    .map(producto -> Response.ok(producto).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity("Producto con ID " + id + " no encontrado")
                            .build());
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al obtener el producto: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en la base de datos")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @APIResponse(responseCode = "400", description = "Datos del producto inválidos"),
            @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response crearProducto(Producto producto) {
        if (producto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El cuerpo de la solicitud no puede estar vacío")
                    .build();
        }

        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return Response.status(Response.Status.CREATED)
                    .entity(nuevoProducto)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear el producto: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar un producto", description = "Actualiza todos los campos de un producto existente")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @APIResponse(responseCode = "400", description = "Datos del producto inválidos"),
            @APIResponse(responseCode = "404", description = "Producto no encontrado"),
            @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarProducto(@PathParam("id") int id, Producto producto) {
        if (producto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El cuerpo de la solicitud no puede estar vacío")
                    .build();
        }

        producto.setId(id);

        try {
            boolean actualizado = productoService.actualizarProducto(producto);
            if (actualizado) {
                return Response.ok()
                        .entity("Producto actualizado exitosamente")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Producto con ID " + id + " no encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al actualizar el producto: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Actualizar parcialmente un producto", description = "Actualiza solo los campos especificados de un producto existente")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Producto actualizado parcialmente con éxito"),
            @APIResponse(responseCode = "400", description = "Datos del producto inválidos"),
            @APIResponse(responseCode = "404", description = "Producto no encontrado"),
            @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response actualizarProductoParcial(@PathParam("id") int id, Producto producto) {
        if (producto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El cuerpo de la solicitud no puede estar vacío")
                    .build();
        }

        try {
            boolean actualizado = productoService.actualizarProductoParcial(id, producto);
            if (actualizado) {
                return Response.ok()
                        .entity("Producto actualizado parcialmente con éxito")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Producto con ID " + id + " no encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al actualizar parcialmente el producto: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto existente basado en su ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @APIResponse(responseCode = "404", description = "Producto no encontrado"),
            @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarProducto(@PathParam("id") int id) {
        try {
            boolean eliminado = productoService.eliminarProducto(id);
            if (eliminado) {
                return Response.ok()
                        .entity("Producto eliminado exitosamente")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Producto con ID " + id + " no encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al eliminar el producto: " + e.getMessage())
                    .build();
        }
    }
}