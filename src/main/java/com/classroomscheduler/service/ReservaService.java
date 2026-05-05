package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateReservaRequest;
import com.classroomscheduler.exception.ConflitoException;
import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.HorarioReserva;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.StatusReserva;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.ReservaRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
        reserva.setStatus(StatusReserva.PENDENTE);
        return reservaRepository.save(reserva);
    }

    public Reserva cancelar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.setCancelada(true);
        reserva.setStatus(StatusReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public Reserva aprovar(Long id) {
        Reserva reserva = buscarPorId(id);
        if (reserva.isCancelada()) {
            throw new RegraDeNegocioException("Reserva cancelada nao pode ser aprovada.");
        }

        boolean conflito = reservaRepository.findAll().stream()
                .filter(outra -> !outra.getId().equals(reserva.getId()))
                .filter(outra -> !outra.isCancelada())
                .filter(outra -> outra.getStatus() == StatusReserva.APROVADA)
                .filter(outra -> outra.getEspaco().getId().equals(reserva.getEspaco().getId()))
                .anyMatch(outra -> outra.getHorarios().getInicio().isBefore(reserva.getHorarios().getFim())
                        && outra.getHorarios().getFim().isAfter(reserva.getHorarios().getInicio()));

        if (conflito) {
            throw new ConflitoException("Conflito de horario com reserva ja aprovada.");
        }

        reserva.setStatus(StatusReserva.APROVADA);
        return reservaRepository.save(reserva);
    }

    public Reserva recusar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.setStatus(StatusReserva.RECUSADA);
        reserva.setCancelada(true);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listarPorEspacoEData(Long espacoId, LocalDate data) {
        espacoService.buscarPorId(espacoId);
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay().minusNanos(1);
        return reservaRepository.findByEspacoIdAndCanceladaFalseAndHorariosInicioBetweenOrderByHorariosInicioAsc(
                espacoId,
                inicioDia,
                fimDia
        );
    }

    public List<Reserva> filtrar(
            String status,
            Long requesterId,
            Long spaceId,
            LocalDate date,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Stream<Reserva> stream = reservaRepository.findAll().stream();

        if (status != null && !status.isBlank()) {
            boolean active = "ACTIVE".equalsIgnoreCase(status);
            stream = stream.filter(reserva -> active != reserva.isCancelada());
        }
        if (requesterId != null) {
            stream = stream.filter(reserva -> reserva.getSolicitante().getId().equals(requesterId));
        }
        if (spaceId != null) {
            stream = stream.filter(reserva -> reserva.getEspaco().getId().equals(spaceId));
        }
        if (date != null) {
            LocalDate finalDate = date;
            stream = stream.filter(reserva -> reserva.getHorarios().getInicio().toLocalDate().equals(finalDate));
        }
        if (from != null && to != null) {
            stream = stream.filter(reserva ->
                    reserva.getHorarios().getInicio().isBefore(to)
                            && reserva.getHorarios().getFim().isAfter(from)
            );
        }

        return stream.toList();
    }
}
