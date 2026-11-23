package cl.duoc.nutriexpert.mediciones.controller;

import cl.duoc.nutriexpert.mediciones.dto.*;
import cl.duoc.nutriexpert.mediciones.service.MedicionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mediciones")
public class MedicionController {

    private final MedicionService service;

    public MedicionController(MedicionService service) {
        this.service = service;
    }

    @GetMapping
    public List<MedicionResponse> listar(@RequestParam(required = false) Integer predioId) {
        return service.listar(predioId);
    }

    @GetMapping("/resumen")
    public ResumenResponse resumen() {
        return service.resumen();
    }

    @GetMapping("/{id}")
    public MedicionResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicionResponse crear(@Valid @RequestBody MedicionRequest req) {
        return service.crear(req);
    }

    /** Endpoint que usa la app Android para subir su lote de mediciones pendientes (idempotente). */
    @PostMapping("/sync")
    public List<MedicionResponse> sincronizar(@RequestBody List<MedicionRequest> mediciones) {
        return service.sincronizar(mediciones);
    }

    @PatchMapping("/{id}/validar")
    public MedicionResponse validar(@PathVariable Long id) {
        return service.validar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
