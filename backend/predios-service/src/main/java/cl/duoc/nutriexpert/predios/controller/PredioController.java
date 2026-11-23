package cl.duoc.nutriexpert.predios.controller;

import cl.duoc.nutriexpert.predios.dto.*;
import cl.duoc.nutriexpert.predios.service.PredioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predios")
public class PredioController {

    private final PredioService service;

    public PredioController(PredioService service) {
        this.service = service;
    }

    @GetMapping
    public List<PredioResponse> listar(@RequestParam(required = false) String nombre) {
        return service.listar(nombre);
    }

    @GetMapping("/resumen")
    public ResumenResponse resumen() {
        return service.resumen();
    }

    @GetMapping("/{id}")
    public PredioResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PredioResponse crear(@Valid @RequestBody PredioRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public PredioResponse actualizar(@PathVariable Long id, @Valid @RequestBody PredioRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    /** Endpoint que usa la app Android para subir sus predios (idempotente). */
    @PostMapping("/sync")
    public List<PredioResponse> sincronizar(@RequestBody List<PredioSyncRequest> predios) {
        return service.sincronizar(predios);
    }
}
