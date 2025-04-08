document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('usuarioForm');
    const tableBody = document.querySelector('#usuariosTable tbody');
    const btnCancelar = document.getElementById('btnCancelar');

    let editMode = false;
    let currentId = null;

    // Cargar usuarios al iniciar
    cargarUsuarios();

    // Manejar envío del formulario
    form.addEventListener('submit', function(e) {
        e.preventDefault();

        const usuario = {
            nombre: document.getElementById('nombre').value,
            correo: document.getElementById('correo').value
        };

        if (editMode) {
            actualizarUsuario(currentId, usuario);
        } else {
            crearUsuario(usuario);
        }
    });

    // Cancelar edición
    btnCancelar.addEventListener('click', function() {
        resetForm();
    });

    // Función para cargar usuarios
    function cargarUsuarios() {
        fetch('/api/usuarios')
            .then(response => response.json())
            .then(data => {
                tableBody.innerHTML = '';
                data.forEach(usuario => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${usuario.id}</td>
                        <td>${usuario.nombre}</td>
                        <td>${usuario.correo}</td>
                        <td>
                            <button onclick="editarUsuario(${usuario.id})">Editar</button>
                            <button onclick="eliminarUsuario(${usuario.id})">Eliminar</button>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });
            })
            .catch(error => console.error('Error:', error));
    }

    // Función para crear usuario
    function crearUsuario(usuario) {
        fetch('/api/usuarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(usuario)
        })
            .then(response => response.json())
            .then(() => {
                cargarUsuarios();
                resetForm();
            })
            .catch(error => console.error('Error:', error));
    }

    // Función para actualizar usuario
    function actualizarUsuario(id, usuario) {
        fetch(`/api/usuarios/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(usuario)
        })
            .then(response => response.json())
            .then(() => {
                cargarUsuarios();
                resetForm();
            })
            .catch(error => console.error('Error:', error));
    }

    // Función para eliminar usuario
    window.eliminarUsuario = function(id) {
        if (confirm('¿Estás seguro de eliminar este usuario?')) {
            fetch(`/api/usuarios/${id}`, {
                method: 'DELETE'
            })
                .then(() => cargarUsuarios())
                .catch(error => console.error('Error:', error));
        }
    };

    // Función para editar usuario
    window.editarUsuario = function(id) {
        fetch(`/api/usuarios/${id}`)
            .then(response => response.json())
            .then(usuario => {
                document.getElementById('usuarioId').value = usuario.id;
                document.getElementById('nombre').value = usuario.nombre;
                document.getElementById('correo').value = usuario.correo;

                editMode = true;
                currentId = usuario.id;
                document.getElementById('btnGuardar').textContent = 'Actualizar';
            })
            .catch(error => console.error('Error:', error));
    };

    // Función para resetear el formulario
    function resetForm() {
        form.reset();
        document.getElementById('usuarioId').value = '';
        editMode = false;
        currentId = null;
        document.getElementById('btnGuardar').textContent = 'Guardar';
    }
});