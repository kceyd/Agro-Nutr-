package cl.duoc.nutriexpert.predios.repository;

import cl.duoc.nutriexpert.predios.model.Predio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PredioRepository extends JpaRepository<Predio, Long> {

    // 1) Consulta derivada del nombre del metodo
    Optional<Predio> findByIdLocal(Integer idLocal);

    // 2) Derivada con filtro y orden
    List<Predio> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    // 3) JPQL propia
    @Query("select coalesce(sum(p.superficieHa), 0) from Predio p")
    Double sumarSuperficie();
}
