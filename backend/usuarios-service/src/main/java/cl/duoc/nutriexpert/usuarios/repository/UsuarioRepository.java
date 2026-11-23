package cl.duoc.nutriexpert.usuarios.repository;

import cl.duoc.nutriexpert.usuarios.model.Rol;
import cl.duoc.nutriexpert.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1) Derivada: buscar por correo (login)
    Optional<Usuario> findByCorreo(String correo);

    // 2) Derivada: validar duplicados al registrar
    boolean existsByCorreo(String correo);

    // 3) JPQL propia: listar por perfil
    @Query("select u from Usuario u where u.rol = :rol order by u.nombre")
    List<Usuario> listarPorRol(Rol rol);
}
