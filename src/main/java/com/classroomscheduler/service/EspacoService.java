package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateEspacoRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.Auditorio;
import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.model.Laboratorio;
import com.classroomscheduler.model.Quadra;
import com.classroomscheduler.model.Sala;
import com.classroomscheduler.repository.EspacoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspacoService {

    private final EspacoRepository espacoRepository;
    private final PredioService predioService;

    public EspacoService(EspacoRepository espacoRepository, PredioService predioService) {
        this.espacoRepository = espacoRepository;
        this.predioService = predioService;
    }

    public List<Espaco> listarTodos() {
        return espacoRepository.findAll();
    }

    public List<Espaco> listarDisponiveis() {
        return espacoRepository.findByIndisponivelFalse();
    }

    public List<Espaco> listarPorPredio(Long predioId) {
        return espacoRepository.findByPredioId(predioId);
    }

    public Espaco buscarPorId(Long id) {
        return espacoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Espaco nao encontrado."));
    }

    public Espaco salvar(Espaco espaco) {
        return espacoRepository.save(espaco);
    }

    public Espaco criar(CreateEspacoRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new RegraDeNegocioException("Espaco deve possuir nome.");
        }

        if (request.getTipo() == null || request.getTipo().isBlank()) {
            throw new RegraDeNegocioException("Espaco deve possuir tipo.");
        }

        if (request.getCapacidade() == null || request.getCapacidade() <= 0) {
            throw new RegraDeNegocioException("Espaco deve possuir capacidade valida.");
        }

        if (request.getPredioId() == null) {
            throw new RegraDeNegocioException("Espaco deve possuir predio.");
        }

        Espaco espaco = criarPorTipo(request.getTipo());
        espaco.setNome(request.getNome());
        espaco.setCapacidade(request.getCapacidade());
        espaco.setPredio(predioService.buscarPorId(request.getPredioId()));
        espaco.setIndisponivel(false);
        espaco.setMotivoIndisponibilidade(null);
        return espacoRepository.save(espaco);
    }

    public Espaco atualizarIndisponibilidade(Long id, boolean indisponivel, String motivo) {
        Espaco espaco = buscarPorId(id);
        espaco.setIndisponivel(indisponivel);
        espaco.setMotivoIndisponibilidade(indisponivel ? motivo : null);
        return espacoRepository.save(espaco);
    }

    private Espaco criarPorTipo(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "SALA" -> new Sala();
            case "AUDITORIO" -> new Auditorio();
            case "QUADRA" -> new Quadra();
            case "LABORATORIO" -> new Laboratorio();
            default -> throw new RegraDeNegocioException("Tipo de espaco invalido.");
        };
    }
}
