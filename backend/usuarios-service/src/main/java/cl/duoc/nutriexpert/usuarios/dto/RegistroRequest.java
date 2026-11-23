package cl.duoc.nutriexpert.usuarios.dto;

import cl.duoc.nutriexpert.usuarios.model.Rol;
import jakarta.validation.constraints.*;

/** Reglas iguales a Validacion.kt: 8+ caracteres, una mayuscula y un numero. */
public record RegistroRequest(
        @NotBlank(message = "Ingresa tu nombre")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "Ingresa tu correo")
        @Email(message = "Ingresa un correo válido")
        String correo,

        @NotBlank(message = "Ingresa una contraseña")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "Mínimo 8 caracteres, una mayúscula y un número")
        String password,

        @NotNull(message = "Elige un perfil")
        Rol rol
) {}
