package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateReservaRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.Horarios;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.Solicitante;
import com.classroomscheduler.repository.ReservaRepository;
import com.classroomscheduler.repository.SolicitanteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SolicitanteRepository solicitanteRepository;
    private final EspacoService espacoService;

    public ReservaService(
            ReservaRepository reservaRepository,
            SolicitanteRepository solicitanteRepository,
            EspacoService espacoService
    ) {
        this.reservaRepository = reservaRepository;
        this.solicitanteRepository = solicitanteRepository;
        this.espacoService = espacoService;
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> listarAtivas() {
        return reservaRepository.findByCanceladaFalse();
    }

    public List<Reserva> listarPorSolicitante(Long solicitanteId) {
        return reservaRepository.findBySolicitanteId(solicitanteId);
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva nao encontrada."));
    }

    public Reserva criar(CreateReservaRequest request) {
        if (request.getSolicitanteId() == null) {
            throw new RegraDeNegocioException("Reserva deve possuir solicitante.");
        }

        if (request.getEspacoId() == null) {
            throw new RegraDeNegocioException("Reserva deve possuir espaco.");
        }

        if (request.getInicio() == null || request.getFim() == null) {
            throw new RegraDeNegocioException("Reserva deve possuir horarios.");
        }

        Solicitante solicitante = solicitanteRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante nao encontrado."));

        LocalDateTime inicio = request.getInicio();
        LocalDateTime fim = request.getFim();

        boolean existeConflito = reservaRepository
                .existsByEspacoIdAndCanceladaFalseAndHorariosInicioLessThanAndHorariosFimGreaterThan(
                        request.getEspacoId(),
                        fim,
                        inicio
                );

        if (existeConflito) {
            throw new RegraDeNegocioException("Ja existe reserva para o espaco nesse horario.");
        }

        Horarios horarios = new Horarios();
        horarios.setInicio(inicio);
        horarios.setFim(fim);
        horarios.validar();

        Reserva reserva = new Reserva();
        reserva.setSolicitante(solicitante);
        reserva.setEspaco(espacoService.buscarPorId(request.getEspacoId()));
        reserva.setHorarios(horarios);
        reserva.setMotivo(request.getMotivo());
        reserva.setCancelada(false);
        return reservaRepository.save(reserva);
    }

    public Reserva cancelar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.setCancelada(true);
        return reservaRepository.save(reserva);
    }
}
