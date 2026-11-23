package cl.duoc.nutriexpert.usuarios.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String mensaje,
        Map<String, String> detalles,
        LocalDateTime fecha
) {
    public static ErrorResponse de(int status, String error, String mensaje, Map<String, String> detalles) {
        return new ErrorResponse(status, error, mensaje, detalles, LocalDateTime.now());
    }
}
