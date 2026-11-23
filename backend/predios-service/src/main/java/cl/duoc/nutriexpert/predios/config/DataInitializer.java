package cl.duoc.nutriexpert.predios.config;

import cl.duoc.nutriexpert.predios.model.Predio;
import cl.duoc.nutriexpert.predios.repository.PredioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    /**
     * Carga los mismos 3 predios de ejemplo que trae la app (AppDatabase.kt) con sus mismos
     * idLocal 1, 2 y 3, para que al sincronizar no se dupliquen. Solo corre si la tabla esta vacia.
     */
    @Bean
    CommandLineRunner cargarPredios(PredioRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.saveAll(List.of(
                        new Predio(1, "Predio Los Aromos - Sector A", 4.5),
                        new Predio(2, "Predio Quebrada Verde - Sector 2", 2.1),
                        new Predio(3, "Predio El Canelo - Sector C", 6.8)
                ));
            }
        };
    }
}
