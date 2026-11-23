package cl.duoc.nutriexpert.mediciones.service;

import cl.duoc.nutriexpert.mediciones.dto.*;
import cl.duoc.nutriexpert.mediciones.exception.RecursoNoEncontradoException;
import cl.duoc.nutriexpert.mediciones.model.Medicion;
import cl.duoc.nutriexpert.mediciones.repository.MedicionRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MedicionService {

    private final MedicionRepository repo;
    private final Validator validator;

    public MedicionService(MedicionRepository repo, Validator validator) {
        this.repo = repo;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public List<MedicionResponse> listar(Integer predioId) {
        List<Medicion> lista = (predioId == null)
                ? repo.findAll(Sort.by(Sort.Direction.DESC, "fechaMillis"))
                : repo.findByPredioIdOrderByFechaMillisDesc(predioId);
        return lista.stream().map(MedicionResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public MedicionResponse obtener(Long id) {
        return MedicionResponse.desde(buscar(id));
    }

    @Transactional
    public MedicionResponse crear(MedicionRequest req) {
        return MedicionResponse.desde(repo.save(aplicar(new Medicion(), req)));
    }

    /**
     * Recibe el lote de la app y guarda sin duplicar: si ya existe una medicion con ese idLocal
     * la actualiza (por ejemplo cuando el asesor la valida y la app reenvia el cambio).
     * Si una medicion del lote es invalida se rechaza todo el lote (no se guarda nada a medias).
     */
    @Transactional
    public List<MedicionResponse> sincronizar(List<MedicionRequest> lista) {
        for (MedicionRequest req : lista) {
            Set<ConstraintViolation<MedicionRequest>> errores = validator.validate(req);
            if (!errores.isEmpty()) {
                String detalle = errores.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; "));
                throw new IllegalArgumentException("Medición inválida (idLocal=" + req.idLocal() + "): " + detalle);
            }
        }
        return lista.stream().map(req -> {
            Medicion m = (req.idLocal() == null)
                    ? new Medicion()
                    : repo.findByIdLocal(req.idLocal()).orElseGet(Medicion::new);
            return MedicionResponse.desde(repo.save(aplicar(m, req)));
        }).toList();
    }

    @Transactional
    public MedicionResponse validar(Long id) {
        Medicion m = buscar(id);
        m.setValidada(true);
        return MedicionResponse.desde(repo.save(m));
    }

    @Transactional
    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public ResumenResponse resumen() {
        long validadas = repo.countByValidada(true);
        long sinValidar = repo.countByValidada(false);
        List<ResumenResponse.PorCultivo> porCultivo = repo.contarPorCultivo().stream()
                .map(c -> new ResumenResponse.PorCultivo(c.getCultivo(), c.getTotal()))
                .toList();
        return new ResumenResponse(validadas + sinValidar, validadas, sinValidar, porCultivo);
    }

    private Medicion aplicar(Medicion m, MedicionRequest r) {
        m.setIdLocal(r.idLocal());
        m.setPredioId(r.predioId());
        m.setCultivo(r.cultivo().trim());
        m.setEtapa(r.etapa().trim());
        m.setPh(r.ph());
        m.setCe(r.ce());
        m.setN(r.n());
        m.setP(r.p());
        m.setK(r.k());
        m.setCa(r.ca());
        m.setMg(r.mg());
        m.setFechaMillis(r.fechaMillis());
        m.setValidada(r.validada());
        return m;
    }

    private Medicion buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la medición con id " + id));
    }
}
