package cl.duoc.nutriexpert.mediciones.dto;

import cl.duoc.nutriexpert.mediciones.model.Medicion;
import java.time.LocalDateTime;

public record MedicionResponse(
        Long id, Integer idLocal, Integer predioId, String cultivo, String etapa,
        Double ph, Double ce, Double n, Double p, Double k, Double ca, Double mg,
        Long fechaMillis, boolean validada, LocalDateTime recibidoEn
) {
    public static MedicionResponse desde(Medicion m) {
        return new MedicionResponse(m.getId(), m.getIdLocal(), m.getPredioId(), m.getCultivo(), m.getEtapa(),
                m.getPh(), m.getCe(), m.getN(), m.getP(), m.getK(), m.getCa(), m.getMg(),
                m.getFechaMillis(), m.isValidada(), m.getRecibidoEn());
    }
}
