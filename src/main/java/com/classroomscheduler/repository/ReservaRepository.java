package com.classroomscheduler.repository;

import com.classroomscheduler.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findBySolicitanteId(Long solicitanteId);

    List<Reserva> findByEspacoId(Long espacoId);

    List<Reserva> findByCanceladaFalse();

    boolean existsByEspacoIdAndCanceladaFalseAndHorariosInicioLessThanAndHorariosFimGreaterThan(
            Long espacoId,
            LocalDateTime fim,
            LocalDateTime inicio
    );

    boolean existsBySolicitanteIdAndEspacoIdAndMotivo(
            Long solicitanteId,
            Long espacoId,
            String motivo
    );

    Optional<Reserva> findFirstBySolicitanteIdAndEspacoIdAndMotivo(
            Long solicitanteId,
            Long espacoId,
            String motivo
    );
}
