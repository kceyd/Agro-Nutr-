package cl.duoc.nutriexpert.usuarios.dto;

import cl.duoc.nutriexpert.usuarios.model.Rol;
import cl.duoc.nutriexpert.usuarios.model.Usuario;
import java.time.LocalDateTime;

/** Nunca incluye la contrasena ni su hash. */
public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        Rol rol,
        boolean activo,
        LocalDateTime creadoEn
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol(), u.isActivo(), u.getCreadoEn());
    }
}
