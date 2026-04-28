package com.classroomscheduler.repository;

import com.classroomscheduler.model.Predio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PredioRepository extends JpaRepository<Predio, Long> {

    Optional<Predio> findByCodigo(String codigo);
}
