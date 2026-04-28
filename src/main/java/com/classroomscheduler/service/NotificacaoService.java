package com.classroomscheduler.service;

import com.classroomscheduler.model.Notificacao;
import com.classroomscheduler.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<Notificacao> listarTodas() {
        return notificacaoRepository.findAll();
    }

    public List<Notificacao> listarPorDestinatario(Long destinatarioId) {
        return notificacaoRepository.findByDestinatarioId(destinatarioId);
    }

    public List<Notificacao> listarNaoLidas(Long destinatarioId) {
        return notificacaoRepository.findByDestinatarioIdAndLidaFalse(destinatarioId);
    }

    public Notificacao buscarPorId(Long id) {
        return notificacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notificacao nao encontrada."));
    }

    public Notificacao salvar(Notificacao notificacao) {
        return notificacaoRepository.save(notificacao);
    }

    public Notificacao marcarComoLida(Long id) {
        Notificacao notificacao = buscarPorId(id);
        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }
}
