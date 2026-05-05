package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateSolicitanteRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.Aluno;
import com.classroomscheduler.model.Funcionario;
import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.AlunoRepository;
import com.classroomscheduler.repository.FuncionarioRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SolicitanteService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public SolicitanteService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            FuncionarioRepository funcionarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findByPapel(PapelUsuario.SOLICITANTE);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getPapel() == PapelUsuario.SOLICITANTE)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante nao encontrado."));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailAndPapel(email.trim().toLowerCase(Locale.ROOT), PapelUsuario.SOLICITANTE)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante nao encontrado."));
    }

    public Usuario criar(CreateSolicitanteRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new RegraDeNegocioException("Solicitante deve possuir nome.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RegraDeNegocioException("Solicitante deve possuir email.");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RegraDeNegocioException("Ja existe solicitante com esse email.");
        }

        TipoSolicitante tipoSolicitante = inferirTipoSolicitante(email);
        Usuario solicitante = criarSolicitante(tipoSolicitante, request);
        solicitante.setNome(request.getNome());
        solicitante.setEmail(email);
        return salvarSolicitante(solicitante);
    }

    public void remover(Long id) {
        Usuario solicitante = buscarPorId(id);
        usuarioRepository.delete(solicitante);
    }

    private TipoSolicitante inferirTipoSolicitante(String email) {
        if (email.endsWith("@al.insper.edu.br")) {
            return TipoSolicitante.ALUNO;
        }

        if (email.endsWith("@insper.edu.br")) {
            return TipoSolicitante.FUNCIONARIO;
        }

        throw new RegraDeNegocioException("Email deve ser institucional do Insper.");
    }

    private Usuario criarSolicitante(TipoSolicitante tipoSolicitante, CreateSolicitanteRequest request) {
        if (tipoSolicitante == TipoSolicitante.ALUNO) {
            Aluno aluno = new Aluno();
            aluno.setNumeroMatricula(request.getNumeroMatricula());
            return aluno;
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNumeroCracha(request.getNumeroCracha());
        funcionario.setCargo(request.getCargo());
        return funcionario;
    }

    private Usuario salvarSolicitante(Usuario solicitante) {
        if (solicitante instanceof Aluno aluno) {
            return alunoRepository.save(aluno);
        }

        if (solicitante instanceof Funcionario funcionario) {
            return funcionarioRepository.save(funcionario);
        }

        throw new RegraDeNegocioException("Tipo de solicitante invalido.");
    }
}
