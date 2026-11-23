package cl.duoc.nutriexpert.usuarios.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        // Mensaje unico a proposito: no revela si fallo el correo, la clave o el perfil.
        super("Correo, contraseña o perfil incorrecto");
    }
}
