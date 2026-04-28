package com.classroomscheduler.service;

import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
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
                .orElseThrow(() -> new NoSuchElementException("Reserva nao encontrada."));
    }

    public Reserva criar(Reserva reserva) {
        if (reserva.getEspaco() == null || reserva.getEspaco().getId() == null) {
            throw new IllegalArgumentException("Reserva deve possuir espaco.");
        }

        if (reserva.getHorarios() == null) {
            throw new IllegalArgumentException("Reserva deve possuir horarios.");
        }

        LocalDateTime inicio = reserva.getHorarios().getInicio();
        LocalDateTime fim = reserva.getHorarios().getFim();

        boolean existeConflito = reservaRepository
                .existsByEspacoIdAndCanceladaFalseAndHorariosInicioLessThanAndHorariosFimGreaterThan(
                        reserva.getEspaco().getId(),
                        fim,
                        inicio
                );

        if (existeConflito) {
            throw new IllegalArgumentException("Ja existe reserva para o espaco nesse horario.");
        }

        reserva.setCancelada(false);
        return reservaRepository.save(reserva);
    }

    public Reserva cancelar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.setCancelada(true);
        return reservaRepository.save(reserva);
    }
}
