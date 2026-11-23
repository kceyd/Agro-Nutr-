package cl.duoc.nutriexpert.predios.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Predio tal como lo manda la app Android al sincronizar (incluye su id local de Room). */
public record PredioSyncRequest(
        @NotNull(message = "idLocal es obligatorio")
        Integer idLocal,

        @NotBlank @Size(min = 3, max = 120)
        String nombre,

        @NotNull @DecimalMin(value = "0.0", inclusive = false) @DecimalMax("1000.0")
        Double superficieHa
) {}
