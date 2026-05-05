package com.classroomscheduler.service;

import com.classroomscheduler.dto.UpdateUsuarioRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void remover(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    public Usuario atualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = buscarPorId(id);

        if (request.getNome() != null && !request.getNome().isBlank()) {
            usuario.setNome(request.getNome());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            usuario.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        }
        if (request.getTipoSolicitante() != null && !request.getTipoSolicitante().isBlank()) {
            usuario.setTipoSolicitante(parseTipoSolicitante(request.getTipoSolicitante()));
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarStatus(Long id, boolean ativo) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(ativo);
        return usuarioRepository.save(usuario);
    }

    private TipoSolicitante parseTipoSolicitante(String tipoSolicitante) {
        try {
            return TipoSolicitante.valueOf(tipoSolicitante.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new RegraDeNegocioException("Tipo de solicitante invalido.");
        }
    }
}
