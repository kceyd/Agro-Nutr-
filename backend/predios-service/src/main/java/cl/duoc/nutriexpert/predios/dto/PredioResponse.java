package cl.duoc.nutriexpert.predios.dto;

import cl.duoc.nutriexpert.predios.model.Predio;
import java.time.LocalDateTime;

public record PredioResponse(
        Long id,
        Integer idLocal,
        String nombre,
        Double superficieHa,
        LocalDateTime creadoEn
) {
    public static PredioResponse desde(Predio p) {
        return new PredioResponse(p.getId(), p.getIdLocal(), p.getNombre(), p.getSuperficieHa(), p.getCreadoEn());
    }
}
