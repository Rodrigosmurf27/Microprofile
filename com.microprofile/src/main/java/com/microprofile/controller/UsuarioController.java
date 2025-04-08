package com.microprofile.controller;

import com.microprofile.model.Usuario;
import com.microprofile.service.UsuarioService;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    @Inject
    UsuarioService usuarioService;

    @GET
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GET
    @Path("/{id}")
    public Response obtenerUsuario(@PathParam("id") Integer id) {
        Usuario usuario = usuarioService.obtenerUsuario(id);
        if (usuario != null) {
            return Response.ok(usuario).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response crearUsuario(Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        return Response.status(Response.Status.CREATED).entity(nuevoUsuario).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarUsuario(@PathParam("id") Integer id, Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        if (usuarioActualizado != null) {
            return Response.ok(usuarioActualizado).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarUsuario(@PathParam("id") Integer id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}