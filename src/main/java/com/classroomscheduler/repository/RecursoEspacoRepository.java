package com.classroomscheduler.repository;

import com.classroomscheduler.model.RecursoEspaco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecursoEspacoRepository extends JpaRepository<RecursoEspaco, Long> {

    Optional<RecursoEspaco> findByNomeIgnoreCase(String nome);
}
