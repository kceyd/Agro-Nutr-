package cl.duoc.nutriexpert.predios.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos para crear o editar un predio (mismas reglas que Validacion.kt de la app). */
public record PredioRequest(
        @NotBlank(message = "Ingresa un nombre")
        @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
        String nombre,

        @NotNull(message = "Ingresa la superficie")
        @DecimalMin(value = "0.0", inclusive = false, message = "La superficie debe ser mayor a 0")
        @DecimalMax(value = "1000.0", message = "La superficie no puede superar 1000 ha")
        Double superficieHa
) {}
