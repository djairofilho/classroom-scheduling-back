package com.classroomscheduler.repository;

import com.classroomscheduler.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findBySolicitanteId(Long solicitanteId);

    List<Reserva> findByEspacoId(Long espacoId);

    List<Reserva> findByCanceladaFalse();

    boolean existsByEspacoIdAndCanceladaFalseAndHorariosInicioLessThanAndHorariosFimGreaterThan(
            Long espacoId,
            LocalDateTime fim,
            LocalDateTime inicio
    );
}
