package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateSolicitanteRequest;
import com.classroomscheduler.model.Solicitante;
import com.classroomscheduler.repository.SolicitanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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
                .orElseThrow(() -> new NoSuchElementException("Solicitante nao encontrado."));
    }

    public Solicitante buscarPorEmail(String email) {
        return solicitanteRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Solicitante nao encontrado."));
    }

    public Solicitante criar(CreateSolicitanteRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new IllegalArgumentException("Solicitante deve possuir nome.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Solicitante deve possuir email.");
        }

        if (solicitanteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ja existe solicitante com esse email.");
        }

        Solicitante solicitante = new Solicitante();
        solicitante.setNome(request.getNome());
        solicitante.setEmail(request.getEmail());
        return solicitanteRepository.save(solicitante);
    }
}
