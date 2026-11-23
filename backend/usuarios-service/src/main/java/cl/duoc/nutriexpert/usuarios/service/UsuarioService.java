package cl.duoc.nutriexpert.usuarios.service;

import cl.duoc.nutriexpert.usuarios.dto.*;
import cl.duoc.nutriexpert.usuarios.exception.*;
import cl.duoc.nutriexpert.usuarios.model.Rol;
import cl.duoc.nutriexpert.usuarios.model.Usuario;
import cl.duoc.nutriexpert.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest req) {
        String correo = req.correo().trim().toLowerCase();
        if (repo.existsByCorreo(correo)) {
            throw new CorreoDuplicadoException(correo);
        }
        Usuario u = new Usuario(req.nombre().trim(), correo, req.rol(), encoder.encode(req.password()));
        return UsuarioResponse.desde(repo.save(u));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse login(LoginRequest req) {
        Usuario u = repo.findByCorreo(req.correo().trim().toLowerCase())
                .orElseThrow(CredencialesInvalidasException::new);
        boolean ok = u.isActivo()
                && u.getRol() == req.rol()
                && encoder.matches(req.password(), u.getPasswordHash());
        if (!ok) {
            throw new CredencialesInvalidasException();
        }
        return UsuarioResponse.desde(u);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Rol rol) {
        List<Usuario> lista = (rol == null) ? repo.findAll() : repo.listarPorRol(rol);
        return lista.stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest req) {
        Usuario u = buscar(id);
        u.setNombre(req.nombre().trim());
        u.setRol(req.rol());
        u.setActivo(req.activo());
        return UsuarioResponse.desde(repo.save(u));
    }

    @Transactional
    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    private Usuario buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }
}
