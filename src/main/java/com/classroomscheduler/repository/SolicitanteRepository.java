package com.classroomscheduler.repository;

import com.classroomscheduler.model.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitanteRepository extends JpaRepository<Solicitante, Long> {

    Optional<Solicitante> findByEmail(String email);
}
