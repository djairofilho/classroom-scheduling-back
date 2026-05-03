package com.classroomscheduler.repository;

import com.classroomscheduler.model.Indisponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndisponibilidadeRepository extends JpaRepository<Indisponibilidade, Long> {

    List<Indisponibilidade> findByEspacoId(Long espacoId);
}
