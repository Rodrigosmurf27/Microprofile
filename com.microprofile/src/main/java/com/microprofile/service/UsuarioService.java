package com.microprofile.service;

import com.microprofile.model.Usuario;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;


@ApplicationScoped
public class UsuarioService {

    @PersistenceContext
    EntityManager em;

    public List<Usuario> listarUsuarios() {
        return em.createNamedQuery("Usuario.findAll", Usuario.class).getResultList();
    }

    public Usuario obtenerUsuario(Integer id) {
        return em.find(Usuario.class, id);
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        em.persist(usuario);
        return usuario;
    }

    @Transactional
    public Usuario actualizarUsuario(Integer id, Usuario usuario) {
        Usuario usuarioExistente = obtenerUsuario(id);
        if (usuarioExistente != null) {
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setCorreo(usuario.getCorreo());
            em.merge(usuarioExistente);
        }
        return usuarioExistente;
    }

    @Transactional
    public boolean eliminarUsuario(Integer id) {
        Usuario usuario = obtenerUsuario(id);
        if (usuario != null) {
            em.remove(usuario);
            return true;
        }
        return false;
    }
}