package com.microprofile.service;

import com.microprofile.data.Producto;
import com.microprofile.data.ProductoDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

@ApplicationScoped
public class ProductoService {

    @Inject
    private ProductoDAO productoDAO;

    @Counted(name = "productos.contadorCrear", description = "Número de productos creados")
    @Timed(name = "productos.tiempoCrear", description = "Tiempo para crear un producto")
    public Producto crearProducto(Producto producto) throws SQLException {
        return productoDAO.crear(producto);
    }

    @Counted(name = "productos.contadorListar", description = "Número de veces que se ha listado productos")
    @Timed(name = "productos.tiempoListar", description = "Tiempo para listar todos los productos")
    public List<Producto> listarProductos() throws SQLException {
        return productoDAO.obtenerTodos();
    }

    @Counted(name = "productos.contadorObtener", description = "Número de consultas de producto por ID")
    @Timed(name = "productos.tiempoObtener", description = "Tiempo para obtener un producto por ID")
    public Optional<Producto> obtenerProducto(int id) throws SQLException {
        return productoDAO.obtenerPorId(id);
    }

    @Counted(name = "productos.contadorActualizar", description = "Número de productos actualizados")
    @Timed(name = "productos.tiempoActualizar", description = "Tiempo para actualizar un producto")
    public boolean actualizarProducto(Producto producto) throws SQLException {
        return productoDAO.actualizar(producto);
    }

    @Counted(name = "productos.contadorActualizarParcial", description = "Número de productos actualizados parcialmente")
    @Timed(name = "productos.tiempoActualizarParcial", description = "Tiempo para actualizar parcialmente un producto")
    public boolean actualizarProductoParcial(int id, Producto producto) throws SQLException {
        return productoDAO.actualizarParcial(id, producto);
    }

    @Counted(name = "productos.contadorEliminar", description = "Número de productos eliminados")
    @Timed(name = "productos.tiempoEliminar", description = "Tiempo para eliminar un producto")
    public boolean eliminarProducto(int id) throws SQLException {
        return productoDAO.eliminar(id);
    }
}