package cl.duoc.nutriexpert.usuarios.dto;

import cl.duoc.nutriexpert.usuarios.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @NotBlank @Size(min = 3, max = 100) String nombre,
        @NotNull Rol rol,
        @NotNull Boolean activo
) {}
