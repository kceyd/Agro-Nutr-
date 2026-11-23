package cl.duoc.nutriexpert.mediciones.config;

import cl.duoc.nutriexpert.mediciones.dto.MedicionRequest;
import cl.duoc.nutriexpert.mediciones.repository.MedicionRepository;
import cl.duoc.nutriexpert.mediciones.service.MedicionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    /** Dos mediciones de ejemplo (predio 1) para ver datos en Swagger. Solo corre si la tabla esta vacia. */
    @Bean
    CommandLineRunner cargarMediciones(MedicionRepository repo, MedicionService service) {
        return args -> {
            if (repo.count() == 0) {
                long ahora = System.currentTimeMillis();
                service.sincronizar(List.of(
                        new MedicionRequest(null, 1, "Palto", "Floración",
                                6.5, 1.2, 25.0, 15.0, 150.0, 120.0, 40.0, ahora - 86_400_000L, true),
                        new MedicionRequest(null, 1, "Palto", "Engorde",
                                6.8, 1.4, 30.0, 18.0, 190.0, 140.0, 48.0, ahora, false)
                ));
            }
        };
    }
}
