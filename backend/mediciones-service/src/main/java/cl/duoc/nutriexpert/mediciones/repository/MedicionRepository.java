package cl.duoc.nutriexpert.mediciones.repository;

import cl.duoc.nutriexpert.mediciones.model.Medicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicionRepository extends JpaRepository<Medicion, Long> {

    // 1) Derivada: para el upsert al sincronizar
    Optional<Medicion> findByIdLocal(Integer idLocal);

    // 2) Derivada: historial de un predio, la mas reciente primero
    List<Medicion> findByPredioIdOrderByFechaMillisDesc(Integer predioId);

    // 3) JPQL propia: conteo por cultivo para el resumen
    @Query("select m.cultivo as cultivo, count(m) as total from Medicion m group by m.cultivo order by m.cultivo")
    List<CultivoTotal> contarPorCultivo();

    long countByValidada(boolean validada);
}
