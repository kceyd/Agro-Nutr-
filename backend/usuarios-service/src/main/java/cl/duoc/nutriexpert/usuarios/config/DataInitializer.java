package cl.duoc.nutriexpert.usuarios.config;

import cl.duoc.nutriexpert.usuarios.model.Rol;
import cl.duoc.nutriexpert.usuarios.model.Usuario;
import cl.duoc.nutriexpert.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    /**
     * Dos cuentas de DEMOSTRACION (clave: Demo1234). Borralas antes de usar el sistema de verdad.
     * Solo corre si la tabla esta vacia.
     */
    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                String clave = encoder.encode("Demo1234");
                repo.save(new Usuario("Agricultor Demo", "agricultor@demo.cl", Rol.AGRICULTOR, clave));
                repo.save(new Usuario("Asesor Demo", "asesor@demo.cl", Rol.ASESOR, clave));
            }
        };
    }
}
