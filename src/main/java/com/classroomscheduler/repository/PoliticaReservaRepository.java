package com.classroomscheduler.repository;

import com.classroomscheduler.model.PoliticaReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PoliticaReservaRepository extends JpaRepository<PoliticaReserva, Long> {

    Optional<PoliticaReserva> findByNomeIgnoreCase(String nome);
}
