package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateReservaRequest;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.HorarioReserva;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.ReservaRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacoService espacoService;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            EspacoService espacoService
    ) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
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

        Usuario solicitante = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario solicitante nao encontrado."));

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

        HorarioReserva horarios = new HorarioReserva();
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
