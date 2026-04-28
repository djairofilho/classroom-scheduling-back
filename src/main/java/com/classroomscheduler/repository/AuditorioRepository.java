package com.classroomscheduler.repository;

import com.classroomscheduler.model.Auditorio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditorioRepository extends JpaRepository<Auditorio, Long> {
}
