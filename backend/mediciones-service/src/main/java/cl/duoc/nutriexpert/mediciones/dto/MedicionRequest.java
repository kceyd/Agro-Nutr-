package cl.duoc.nutriexpert.mediciones.dto;

import jakarta.validation.constraints.*;

/**
 * Misma forma que MedicionDto de la app Android. Los rangos son los de Campo (Validacion.kt):
 * pH 3-10, CE 0-10, N/P/Mg 0-500, K/Ca 0-1000.
 */
public record MedicionRequest(
        Integer idLocal,

        @NotNull(message = "predioId es obligatorio")
        Integer predioId,

        @NotBlank(message = "Ingresa el cultivo") @Size(max = 40)
        String cultivo,

        @NotBlank(message = "Ingresa la etapa") @Size(max = 40)
        String etapa,

        @NotNull @DecimalMin("3.0") @DecimalMax("10.0") Double ph,
        @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double ce,
        @NotNull @DecimalMin("0.0") @DecimalMax("500.0") Double n,
        @NotNull @DecimalMin("0.0") @DecimalMax("500.0") Double p,
        @NotNull @DecimalMin("0.0") @DecimalMax("1000.0") Double k,
        @NotNull @DecimalMin("0.0") @DecimalMax("1000.0") Double ca,
        @NotNull @DecimalMin("0.0") @DecimalMax("500.0") Double mg,

        @NotNull(message = "fechaMillis es obligatorio") Long fechaMillis,
        boolean validada
) {}
