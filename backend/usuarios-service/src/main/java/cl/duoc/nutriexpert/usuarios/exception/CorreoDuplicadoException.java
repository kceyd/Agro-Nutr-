package cl.duoc.nutriexpert.usuarios.exception;

public class CorreoDuplicadoException extends RuntimeException {
    public CorreoDuplicadoException(String correo) {
        super("Este correo ya está registrado: " + correo);
    }
}
