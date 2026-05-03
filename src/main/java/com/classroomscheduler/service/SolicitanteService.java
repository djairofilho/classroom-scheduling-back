package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateSolicitanteRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.Solicitante;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.repository.SolicitanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SolicitanteService {

    private final SolicitanteRepository solicitanteRepository;

    public SolicitanteService(SolicitanteRepository solicitanteRepository) {
        this.solicitanteRepository = solicitanteRepository;
    }

    public List<Solicitante> listarTodos() {
        return solicitanteRepository.findAll();
    }

    public Solicitante buscarPorId(Long id) {
        return solicitanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante nao encontrado."));
    }

    public Solicitante buscarPorEmail(String email) {
        return solicitanteRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante nao encontrado."));
    }

    public Solicitante criar(CreateSolicitanteRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new RegraDeNegocioException("Solicitante deve possuir nome.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RegraDeNegocioException("Solicitante deve possuir email.");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (solicitanteRepository.findByEmail(email).isPresent()) {
            throw new RegraDeNegocioException("Ja existe solicitante com esse email.");
        }

        Solicitante solicitante = new Solicitante();
        solicitante.setNome(request.getNome());
        solicitante.setEmail(email);
        solicitante.setPapel(PapelUsuario.SOLICITANTE);
        solicitante.setTipoSolicitante(inferirTipoSolicitante(email));
        return solicitanteRepository.save(solicitante);
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
}
