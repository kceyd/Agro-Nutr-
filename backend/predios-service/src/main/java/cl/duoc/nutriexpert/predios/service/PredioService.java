package cl.duoc.nutriexpert.predios.service;

import cl.duoc.nutriexpert.predios.dto.*;
import cl.duoc.nutriexpert.predios.exception.RecursoNoEncontradoException;
import cl.duoc.nutriexpert.predios.model.Predio;
import cl.duoc.nutriexpert.predios.repository.PredioRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PredioService {

    private final PredioRepository repo;
    private final Validator validator;

    public PredioService(PredioRepository repo, Validator validator) {
        this.repo = repo;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public List<PredioResponse> listar(String nombre) {
        List<Predio> predios = (nombre == null || nombre.isBlank())
                ? repo.findAll()
                : repo.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre.trim());
        return predios.stream().map(PredioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public PredioResponse obtener(Long id) {
        return PredioResponse.desde(buscar(id));
    }

    @Transactional
    public PredioResponse crear(PredioRequest req) {
        Predio p = new Predio(null, req.nombre().trim(), req.superficieHa());
        return PredioResponse.desde(repo.save(p));
    }

    @Transactional
    public PredioResponse actualizar(Long id, PredioRequest req) {
        Predio p = buscar(id);
        p.setNombre(req.nombre().trim());
        p.setSuperficieHa(req.superficieHa());
        return PredioResponse.desde(repo.save(p));
    }

    @Transactional
    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    /**
     * Recibe los predios de la app y los guarda sin duplicar: si ya existe uno con ese idLocal
     * lo actualiza, si no lo crea. Se puede reenviar la misma lista cuantas veces se quiera.
     */
    @Transactional
    public List<PredioResponse> sincronizar(List<PredioSyncRequest> lista) {
        for (PredioSyncRequest req : lista) {
            Set<ConstraintViolation<PredioSyncRequest>> errores = validator.validate(req);
            if (!errores.isEmpty()) {
                String detalle = errores.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; "));
                throw new IllegalArgumentException("Predio inválido (idLocal=" + req.idLocal() + "): " + detalle);
            }
        }
        return lista.stream().map(req -> {
            Predio p = repo.findByIdLocal(req.idLocal())
                    .orElseGet(() -> new Predio(req.idLocal(), req.nombre(), req.superficieHa()));
            p.setNombre(req.nombre().trim());
            p.setSuperficieHa(req.superficieHa());
            return PredioResponse.desde(repo.save(p));
        }).toList();
    }

    @Transactional(readOnly = true)
    public ResumenResponse resumen() {
        return new ResumenResponse(repo.count(), repo.sumarSuperficie());
    }

    private Predio buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el predio con id " + id));
    }
}
