package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateReservaRequest;
import com.classroomscheduler.dto.ReservaLoteConflictResponse;
import com.classroomscheduler.dto.ReservaLoteRequest;
import com.classroomscheduler.dto.ReservaLoteResponse;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class ReservaService {
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");

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
        if (!solicitante.isAtivo()) {
            throw new RegraDeNegocioException("Solicitante inativo.");
        }

        if (espacoService.buscarPorId(request.getEspacoId()).isIndisponivel()) {
            throw new RegraDeNegocioException("Espaco indisponivel para reserva.");
        }

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
        if (reserva.getStatus() != StatusReserva.PENDENTE && reserva.getStatus() != StatusReserva.APROVADA) {
            throw new RegraDeNegocioException("Somente reservas pendentes ou aprovadas podem ser canceladas.");
        }
        reserva.setCancelada(true);
        reserva.setStatus(StatusReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public Reserva aprovar(Long id) {
        Reserva reserva = buscarPorId(id);
        if (reserva.getStatus() != StatusReserva.PENDENTE) {
            throw new RegraDeNegocioException("Somente reservas pendentes podem ser aprovadas.");
        }

        boolean conflito = reservaRepository.findAll().stream()
                .filter(outra -> !outra.getId().equals(reserva.getId()))
                .filter(this::ocupaHorario)
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
        if (reserva.getStatus() != StatusReserva.PENDENTE) {
            throw new RegraDeNegocioException("Somente reservas pendentes podem ser recusadas.");
        }
        reserva.setStatus(StatusReserva.RECUSADA);
        reserva.setCancelada(true);
        return reservaRepository.save(reserva);
    }

    public ReservaLoteResponse criarLote(ReservaLoteRequest request) {
        validarLote(request);

        Usuario solicitante = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario solicitante nao encontrado."));
        if (!solicitante.isAtivo()) {
            throw new RegraDeNegocioException("Solicitante inativo.");
        }

        var espaco = espacoService.buscarPorId(request.getEspacoId());
        if (espaco.isIndisponivel()) {
            throw new RegraDeNegocioException("Espaco indisponivel para reserva.");
        }

        List<Long> idsCriados = new ArrayList<>();
        List<ReservaLoteConflictResponse> conflitos = new ArrayList<>();

        LocalDate data = request.getDataInicio();
        while (!data.isAfter(request.getDataFim())) {
            int diaSemana = data.getDayOfWeek().getValue();
            if (request.getDiasSemana().contains(diaSemana)) {
                LocalDateTime inicio = LocalDateTime.of(data, request.getHoraInicio());
                LocalDateTime fim = LocalDateTime.of(data, request.getHoraFim());

                boolean conflito = reservaRepository.findAll().stream()
                        .filter(this::ocupaHorario)
                        .filter(r -> r.getEspaco().getId().equals(espaco.getId()))
                        .anyMatch(r -> r.getHorarios().getInicio().isBefore(fim)
                                && r.getHorarios().getFim().isAfter(inicio));

                if (conflito) {
                    conflitos.add(new ReservaLoteConflictResponse(
                            data.toString(),
                            request.getHoraInicio().toString(),
                            request.getHoraFim().toString(),
                            "Conflito de horario"
                    ));
                } else {
                    Reserva reserva = new Reserva();
                    reserva.setSolicitante(solicitante);
                    reserva.setEspaco(espaco);

                    HorarioReserva horario = new HorarioReserva();
                    horario.setInicio(inicio.atZone(DEFAULT_ZONE).toLocalDateTime());
                    horario.setFim(fim.atZone(DEFAULT_ZONE).toLocalDateTime());
                    horario.validar();

                    reserva.setHorarios(horario);
                    reserva.setMotivo(request.getMotivo());
                    reserva.setCancelada(false);
                    reserva.setStatus(StatusReserva.APROVADA);
                    Reserva salva = reservaRepository.save(reserva);
                    idsCriados.add(salva.getId());
                }
            }
            data = data.plusDays(1);
        }

        return new ReservaLoteResponse(idsCriados.size(), conflitos.size(), idsCriados, conflitos);
    }

    public List<Reserva> listarPorEspacoEData(Long espacoId, LocalDate data) {
        espacoService.buscarPorId(espacoId);
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay().minusNanos(1);
        return reservaRepository.findByEspacoIdAndCanceladaFalseAndHorariosInicioBetweenOrderByHorariosInicioAsc(
                espacoId,
                inicioDia,
                fimDia
        ).stream().filter(this::ocupaHorario).toList();
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

    private boolean ocupaHorario(Reserva reserva) {
        return reserva.getStatus() == StatusReserva.PENDENTE || reserva.getStatus() == StatusReserva.APROVADA;
    }

    private void validarLote(ReservaLoteRequest request) {
        if (request.getSolicitanteId() == null || request.getEspacoId() == null) {
            throw new RegraDeNegocioException("Solicitante e espaco sao obrigatorios.");
        }
        if (request.getDataInicio() == null || request.getDataFim() == null || request.getDataFim().isBefore(request.getDataInicio())) {
            throw new RegraDeNegocioException("Periodo invalido.");
        }
        if (request.getHoraInicio() == null || request.getHoraFim() == null || !request.getHoraFim().isAfter(request.getHoraInicio())) {
            throw new RegraDeNegocioException("Horario invalido.");
        }
        if (request.getDiasSemana() == null || request.getDiasSemana().isEmpty()) {
            throw new RegraDeNegocioException("Dias da semana sao obrigatorios.");
        }
        boolean diasInvalidos = request.getDiasSemana().stream().anyMatch(dia -> dia < 1 || dia > 7);
        if (diasInvalidos) {
            throw new RegraDeNegocioException("Dias da semana invalidos. Use 1 a 7.");
        }
        if (request.getStatusInicial() != null && !request.getStatusInicial().isBlank()) {
            String status = request.getStatusInicial().trim().toUpperCase(Locale.ROOT);
            if (!"APROVADA".equals(status)) {
                throw new RegraDeNegocioException("Agendamento em lote aceita apenas statusInicial APROVADA.");
            }
        }
        if (request.getAprovacaoAutomatica() != null && !request.getAprovacaoAutomatica()) {
            throw new RegraDeNegocioException("Agendamento em lote requer aprovacaoAutomatica=true.");
        }
    }
}
