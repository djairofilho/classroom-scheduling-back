package com.classroomscheduler.service;

import com.classroomscheduler.dto.CreateNotificacaoRequest;
import com.classroomscheduler.model.Notificacao;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.NotificacaoRepository;
import com.classroomscheduler.repository.ReservaRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository,
            UsuarioRepository usuarioRepository,
            ReservaRepository reservaRepository
    ) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
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

    public Notificacao salvar(CreateNotificacaoRequest request) {
        if (request.getDestinatarioId() == null) {
            throw new IllegalArgumentException("Notificacao deve possuir destinatario.");
        }

        if (request.getMensagem() == null || request.getMensagem().isBlank()) {
            throw new IllegalArgumentException("Notificacao deve possuir mensagem.");
        }

        Usuario destinatario = usuarioRepository.findById(request.getDestinatarioId())
                .orElseThrow(() -> new NoSuchElementException("Usuario destinatario nao encontrado."));

        Notificacao notificacao = new Notificacao();
        notificacao.setDestinatario(destinatario);
        notificacao.setMensagem(request.getMensagem());
        notificacao.setLida(false);

        if (request.getReservaId() != null) {
            Reserva reserva = reservaRepository.findById(request.getReservaId())
                    .orElseThrow(() -> new NoSuchElementException("Reserva nao encontrada."));
            notificacao.setReserva(reserva);
        }

        return notificacaoRepository.save(notificacao);
    }

    public Notificacao marcarComoLida(Long id) {
        Notificacao notificacao = buscarPorId(id);
        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }
}
