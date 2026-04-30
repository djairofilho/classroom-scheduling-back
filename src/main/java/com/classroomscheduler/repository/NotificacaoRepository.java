package com.classroomscheduler.repository;

import com.classroomscheduler.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByDestinatarioId(Long destinatarioId);

    List<Notificacao> findByDestinatarioIdAndLidaFalse(Long destinatarioId);

    boolean existsByDestinatarioIdAndMensagem(Long destinatarioId, String mensagem);
}
