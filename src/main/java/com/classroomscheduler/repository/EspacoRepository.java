package com.classroomscheduler.repository;

import com.classroomscheduler.model.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspacoRepository extends JpaRepository<Espaco, Long> {

    List<Espaco> findByIndisponivelFalse();

    List<Espaco> findByPredioId(Long predioId);

    List<Espaco> findByCapacidadeGreaterThanEqual(Integer capacidade);
}
