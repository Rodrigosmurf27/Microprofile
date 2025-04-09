package com.microprofile.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductoDAO {

    private static final Logger LOGGER = Logger.getLogger(ProductoDAO.class.getName());

    @Inject
    private Connection connection;

    // Crear un nuevo producto
    public Producto crear(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, cantidad) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setBigDecimal(3, producto.getPrecio());
            stmt.setInt(4, producto.getCantidad());

            LOGGER.info("Ejecutando consulta INSERT: " + sql);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("Falló la creación del producto, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    producto.setId(generatedKeys.getInt(1));
                    return obtenerPorId(producto.getId()).orElse(producto);
                } else {
                    throw new SQLException("Falló la creación del producto, no se obtuvo el ID.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear producto: " + e.getMessage(), e);
            throw e;
        }
    }

    // Obtener todos los productos
    public List<Producto> obtenerTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        LOGGER.info("Ejecutando consulta SELECT: " + sql);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            LOGGER.info("Recuperados " + productos.size() + " productos de la base de datos");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener productos: " + e.getMessage(), e);
            throw e;
        }

        return productos;
    }

    // Obtener un producto por su ID
    public Optional<Producto> obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            LOGGER.info("Ejecutando consulta: " + sql + " con ID=" + id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Producto producto = mapearProducto(rs);
                    LOGGER.info("Producto encontrado: " + producto);
                    return Optional.of(producto);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener producto por ID: " + e.getMessage(), e);
            throw e;
        }

        LOGGER.info("No se encontró producto con ID: " + id);
        return Optional.empty();
    }

    // Actualizar un producto
    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, cantidad = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setBigDecimal(3, producto.getPrecio());
            stmt.setInt(4, producto.getCantidad());
            stmt.setInt(5, producto.getId());

            LOGGER.info("Ejecutando actualización para producto ID: " + producto.getId());
            int filasAfectadas = stmt.executeUpdate();
            LOGGER.info("Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar producto: " + e.getMessage(), e);
            throw e;
        }
    }

    // Actualizar parcialmente un producto
    public boolean actualizarParcial(int id, Producto productoActualizado) throws SQLException {
        Optional<Producto> productoExistente = obtenerPorId(id);

        if (!productoExistente.isPresent()) {
            return false;
        }

        Producto producto = productoExistente.get();

        // Actualizar solo los campos no nulos
        if (productoActualizado.getNombre() != null) {
            producto.setNombre(productoActualizado.getNombre());
        }

        if (productoActualizado.getDescripcion() != null) {
            producto.setDescripcion(productoActualizado.getDescripcion());
        }

        if (productoActualizado.getPrecio() != null) {
            producto.setPrecio(productoActualizado.getPrecio());
        }

        if (productoActualizado.getCantidad() > 0) {
            producto.setCantidad(productoActualizado.getCantidad());
        }

        return actualizar(producto);
    }

    // Eliminar un producto
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            LOGGER.info("Ejecutando eliminación para producto ID: " + id);
            int filasAfectadas = stmt.executeUpdate();
            LOGGER.info("Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar producto: " + e.getMessage(), e);
            throw e;
        }
    }

    // Mapear ResultSet a objeto Producto
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        try {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            String descripcion = rs.getString("descripcion");
            BigDecimal precio = rs.getBigDecimal("precio");
            int cantidad = rs.getInt("cantidad");

            Timestamp timestamp = rs.getTimestamp("fecha_creacion");
            LocalDateTime fechaCreacion = timestamp != null ? timestamp.toLocalDateTime() : null;

            return new Producto(id, nombre, descripcion, precio, cantidad, fechaCreacion);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al mapear resultado a Producto: " + e.getMessage(), e);
            throw e;
        }
    }
}