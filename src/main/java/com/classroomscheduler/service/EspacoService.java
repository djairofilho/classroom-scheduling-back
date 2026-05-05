package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateEspacoRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.model.TipoEspaco;
import com.classroomscheduler.repository.EspacoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

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

        Espaco espaco = new Espaco();
        espaco.setNome(request.getNome());
        espaco.setTipo(parseTipo(request.getTipo()));
        espaco.setCapacidade(request.getCapacidade());
        espaco.setPredio(predioService.buscarPorId(request.getPredioId()));
        espaco.setIndisponivel(false);
        return espacoRepository.save(espaco);
    }

    public Espaco atualizarIndisponibilidade(Long id, boolean indisponivel, String motivo) {
        Espaco espaco = buscarPorId(id);
        espaco.setIndisponivel(indisponivel);
        return espacoRepository.save(espaco);
    }

    private TipoEspaco parseTipo(String tipo) {
        try {
            return TipoEspaco.valueOf(tipo.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new RegraDeNegocioException("Tipo de espaco invalido.");
        }
    }
}
