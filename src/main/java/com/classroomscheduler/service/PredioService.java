package com.classroomscheduler.service;

import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.model.Notificacao;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.Predio;
import com.classroomscheduler.repository.EspacoRepository;
import com.classroomscheduler.repository.NotificacaoRepository;
import com.classroomscheduler.repository.PredioRepository;
import com.classroomscheduler.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PredioService {

    private final PredioRepository predioRepository;
    private final EspacoRepository espacoRepository;
    private final ReservaRepository reservaRepository;
    private final NotificacaoRepository notificacaoRepository;

    public PredioService(
            PredioRepository predioRepository,
            EspacoRepository espacoRepository,
            ReservaRepository reservaRepository,
            NotificacaoRepository notificacaoRepository
    ) {
        this.predioRepository = predioRepository;
        this.espacoRepository = espacoRepository;
        this.reservaRepository = reservaRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<Predio> listarTodos() {
        return predioRepository.findAll();
    }

    public Predio buscarPorId(Long id) {
        return predioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Predio nao encontrado."));
    }

    public Predio buscarPorCodigo(String codigo) {
        return predioRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Predio nao encontrado."));
    }

    public Predio salvar(Predio predio) {
        return predioRepository.save(predio);
    }

    public Predio atualizar(Long id, Predio dados) {
        Predio predio = buscarPorId(id);
        predio.setNome(dados.getNome());
        predio.setCodigo(dados.getCodigo());
        predio.setLocalizacao(dados.getLocalizacao());
        return predioRepository.save(predio);
    }

    public void remover(Long id) {
        Predio predio = buscarPorId(id);
        List<Espaco> espacos = espacoRepository.findByPredioId(predio.getId());
        for (Espaco espaco : espacos) {
            List<Reserva> reservas = reservaRepository.findByEspacoId(espaco.getId());
            for (Reserva reserva : reservas) {
                List<Notificacao> notificacoes = notificacaoRepository.findByReservaId(reserva.getId());
                notificacoes.forEach(notificacao -> notificacao.setReserva(null));
                notificacaoRepository.saveAll(notificacoes);
            }
            reservaRepository.deleteByEspacoId(espaco.getId());
        }
        predioRepository.delete(predio);
    }
}
