package cl.duoc.nutriexpert.mediciones.dto;

import java.util.List;

public record ResumenResponse(
        long total,
        long validadas,
        long sinValidar,
        List<PorCultivo> porCultivo
) {
    public record PorCultivo(String cultivo, long total) {}
}
