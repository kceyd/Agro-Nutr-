package cl.duoc.nutriexpert.usuarios.dto;

import cl.duoc.nutriexpert.usuarios.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank(message = "Ingresa tu correo") String correo,
        @NotBlank(message = "Ingresa tu contraseña") String password,
        @NotNull(message = "Elige un perfil") Rol rol
) {}
