package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateEspacoRequest;
import com.classroomscheduler.dto.UpdateEspacoRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.model.Notificacao;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.TipoEspaco;
import com.classroomscheduler.repository.EspacoRepository;
import com.classroomscheduler.repository.NotificacaoRepository;
import com.classroomscheduler.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class EspacoService {

    private final EspacoRepository espacoRepository;
    private final PredioService predioService;
    private final ReservaRepository reservaRepository;
    private final NotificacaoRepository notificacaoRepository;

    public EspacoService(
            EspacoRepository espacoRepository,
            PredioService predioService,
            ReservaRepository reservaRepository,
            NotificacaoRepository notificacaoRepository
    ) {
        this.espacoRepository = espacoRepository;
        this.predioService = predioService;
        this.reservaRepository = reservaRepository;
        this.notificacaoRepository = notificacaoRepository;
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

    public Espaco atualizar(Long id, UpdateEspacoRequest request) {
        Espaco espaco = buscarPorId(id);

        if (request.getNome() != null && !request.getNome().isBlank()) {
            espaco.setNome(request.getNome());
        }
        if (request.getCapacidade() != null && request.getCapacidade() > 0) {
            espaco.setCapacidade(request.getCapacidade());
        }
        if (request.getTipo() != null && !request.getTipo().isBlank()) {
            espaco.setTipo(parseTipo(request.getTipo()));
        }
        if (request.getPredioId() != null) {
            espaco.setPredio(predioService.buscarPorId(request.getPredioId()));
        }

        return espacoRepository.save(espaco);
    }

    public void remover(Long id) {
        Espaco espaco = buscarPorId(id);
        List<Reserva> reservas = reservaRepository.findByEspacoId(espaco.getId());
        for (Reserva reserva : reservas) {
            List<Notificacao> notificacoes = notificacaoRepository.findByReservaId(reserva.getId());
            notificacoes.forEach(notificacao -> notificacao.setReserva(null));
            notificacaoRepository.saveAll(notificacoes);
        }
        reservaRepository.deleteByEspacoId(espaco.getId());
        espacoRepository.delete(espaco);
    }

    private TipoEspaco parseTipo(String tipo) {
        String normalizado = tipo.trim().toUpperCase(Locale.ROOT);
        if ("LAB".equals(normalizado)) {
            return TipoEspaco.LABORATORIO;
        }
        if ("REUNIAO".equals(normalizado)) {
            return TipoEspaco.SALA;
        }
        try {
            return TipoEspaco.valueOf(normalizado);
        } catch (IllegalArgumentException exception) {
            throw new RegraDeNegocioException("Tipo de espaco invalido.");
        }
    }
}
