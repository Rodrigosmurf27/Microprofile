// Constantes para la API
const API_URL = '/com.microprofile/api/productos';
const HTTP_STATUS = {
    OK: 200,
    CREATED: 201,
    BAD_REQUEST: 400,
    NOT_FOUND: 404,
    SERVER_ERROR: 500
};

// Referencias a elementos del DOM
const tablaProductosBody = document.querySelector('#tablaProductos tbody');
const productoModal = document.getElementById('productoModal');
const productoForm = document.getElementById('productoForm');
const productoModalLabel = document.getElementById('productoModalLabel');
const productoId = document.getElementById('productoId');
const nombreInput = document.getElementById('nombre');
const descripcionInput = document.getElementById('descripcion');
const precioInput = document.getElementById('precio');
const cantidadInput = document.getElementById('cantidad');
const guardarProductoBtn = document.getElementById('guardarProducto');
const confirmarEliminarModal = document.getElementById('confirmarEliminarModal');
const confirmarEliminarBtn = document.getElementById('confirmarEliminar');

// Variable para almacenar el ID del producto a eliminar
let productoIdAEliminar = null;
// Variable para seguimiento de intentos de carga
let intentosDeCarga = 0;
const MAX_INTENTOS = 3;

// Modal de Bootstrap
const modal = new bootstrap.Modal(productoModal);
const modalEliminar = new bootstrap.Modal(confirmarEliminarModal);

// Cargar productos al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    cargarProductos();
});

// Evento para guardar producto (crear o actualizar)
guardarProductoBtn.addEventListener('click', () => {
    if (productoForm.checkValidity()) {
        const producto = {
            nombre: nombreInput.value.trim(),
            descripcion: descripcionInput.value.trim(),
            precio: parseFloat(precioInput.value),
            cantidad: parseInt(cantidadInput.value)
        };

        if (productoId.value) {
            // Actualizar producto existente
            actualizarProducto(parseInt(productoId.value), producto);
        } else {
            // Crear nuevo producto
            crearProducto(producto);
        }
    } else {
        productoForm.reportValidity();
    }
});

// Evento para confirmar eliminación
confirmarEliminarBtn.addEventListener('click', () => {
    if (productoIdAEliminar) {
        eliminarProducto(productoIdAEliminar);
    }
});

// Función para cargar todos los productos con reintentos
async function cargarProductos() {
    intentosDeCarga++;

    try {
        tablaProductosBody.innerHTML = '<tr><td colspan="7" class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Cargando...</span></div></td></tr>';

        console.log(`Intento de carga #${intentosDeCarga}`);

        const response = await fetch(API_URL);

        if (!response.ok) {
            const errorText = await response.text();
            console.error(`Error HTTP ${response.status}: ${errorText}`);
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const productos = await response.json();

        if (Array.isArray(productos) && productos.length > 0) {
            mostrarProductosEnTabla(productos);
        } else {
            if (intentosDeCarga === 1) {
                // Primera carga - intentar crear datos de ejemplo
                mostrarMensaje('No hay productos en la base de datos. Creando datos de ejemplo...', 'info');
                await crearDatosEjemplo();
                // Volver a cargar después de crear datos
                setTimeout(cargarProductos, 1000);
            } else {
                tablaProductosBody.innerHTML = '<tr><td colspan="7" class="text-center">No hay productos disponibles</td></tr>';
            }
        }
    } catch (error) {
        console.error('Error al cargar productos:', error);

        if (intentosDeCarga < MAX_INTENTOS) {
            // Reintento con retardo incremental
            const retraso = intentosDeCarga * 1000;
            mostrarMensaje(`Error al cargar productos. Reintentando en ${retraso/1000} segundos...`, 'warning');
            tablaProductosBody.innerHTML = `<tr><td colspan="7" class="text-center">Reintentando conexión (${intentosDeCarga}/${MAX_INTENTOS})...</td></tr>`;
            setTimeout(cargarProductos, retraso);
        } else {
            mostrarMensaje('Error al cargar productos. Por favor, verifique la conexión a la base de datos.', 'danger');
            tablaProductosBody.innerHTML = `
                <tr><td colspan="7" class="text-center text-danger">
                    Error de conexión a la base de datos. 
                    <button class="btn btn-sm btn-outline-primary mt-2" onclick="reiniciarIntentos()">Reintentar</button>
                </td></tr>`;
        }
    }
}

// Función para reiniciar intentos
function reiniciarIntentos() {
    intentosDeCarga = 0;
    cargarProductos();
}

// Función para crear datos de ejemplo
async function crearDatosEjemplo() {
    const productosEjemplo = [
        {
            nombre: "Laptop HP",
            descripcion: "Laptop HP Pavilion 15.6\" con Intel Core i5",
            precio: 12999.99,
            cantidad: 50
        },
        {
            nombre: "Monitor LG",
            descripcion: "Monitor LG 24\" Full HD IPS",
            precio: 3499.99,
            cantidad: 100
        },
        {
            nombre: "Mouse Inalámbrico",
            descripcion: "Mouse inalámbrico ergonómico",
            precio: 499.99,
            cantidad: 75
        }
    ];

    try {
        for (const producto of productosEjemplo) {
            await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(producto)
            });
        }
        mostrarMensaje('Datos de ejemplo creados exitosamente', 'success');
    } catch (error) {
        console.error('Error al crear datos de ejemplo:', error);
        mostrarMensaje('Error al crear datos de ejemplo', 'danger');
    }
}

// Función para mostrar productos en la tabla
function mostrarProductosEnTabla(productos) {
    tablaProductosBody.innerHTML = '';

    productos.forEach(producto => {
        const row = document.createElement('tr');

        // Formatear fecha
        const fecha = producto.fechaCreacion ? new Date(producto.fechaCreacion).toLocaleString('es-ES') : 'N/A';

        row.innerHTML = `
            <td>${producto.id}</td>
            <td>${producto.nombre}</td>
            <td>${producto.descripcion || '-'}</td>
            <td>$${producto.precio.toFixed(2)}</td>
            <td>${producto.cantidad}</td>
            <td>${fecha}</td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button type="button" class="btn btn-editar" data-id="${producto.id}">
                        Editar
                    </button>
                    <button type="button" class="btn btn-eliminar" data-id="${producto.id}">
                        Eliminar
                    </button>
                </div>
            </td>
        `;

        tablaProductosBody.appendChild(row);
    });

    // Agregar eventos a los botones
    document.querySelectorAll('.btn-editar').forEach(btn => {
        btn.addEventListener('click', () => cargarProductoParaEditar(parseInt(btn.dataset.id)));
    });

    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', () => mostrarConfirmacionEliminar(parseInt(btn.dataset.id)));
    });
}

// Funciones restantes se mantienen igual...
// (Crearlas, actualizarlas, eliminarlas, etc.)

// Función para crear un nuevo producto
async function crearProducto(producto) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(producto)
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData || `Error ${response.status}: ${response.statusText}`);
        }

        const nuevoProducto = await response.json();
        mostrarMensaje('Producto creado exitosamente', 'success');
        resetearFormulario();
        modal.hide();
        reiniciarIntentos(); // Reinicia los intentos y vuelve a cargar

    } catch (error) {
        console.error('Error al crear producto:', error);
        mostrarMensaje('Error al crear producto: ' + error.message, 'danger');
    }
}

// Función para cargar un producto para editar
async function cargarProductoParaEditar(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const producto = await response.json();

        // Llenar el formulario
        productoId.value = producto.id;
        nombreInput.value = producto.nombre;
        descripcionInput.value = producto.descripcion || '';
        precioInput.value = producto.precio;
        cantidadInput.value = producto.cantidad;

        // Cambiar título del modal
        productoModalLabel.textContent = 'Editar Producto';

        // Mostrar modal
        modal.show();

    } catch (error) {
        console.error('Error al cargar producto para editar:', error);
        mostrarMensaje('Error al cargar datos del producto', 'danger');
    }
}

// Función para actualizar un producto
async function actualizarProducto(id, producto) {
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(producto)
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData || `Error ${response.status}: ${response.statusText}`);
        }

        mostrarMensaje('Producto actualizado exitosamente', 'success');
        resetearFormulario();
        modal.hide();
        reiniciarIntentos(); // Reinicia los intentos y vuelve a cargar

    } catch (error) {
        console.error('Error al actualizar producto:', error);
        mostrarMensaje('Error al actualizar producto: ' + error.message, 'danger');
    }
}