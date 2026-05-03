package com.classroomscheduler.repository;

import com.classroomscheduler.model.HorarioFuncionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Long> {

    List<HorarioFuncionamento> findByPredioId(Long predioId);

    List<HorarioFuncionamento> findByEspacoId(Long espacoId);
}
