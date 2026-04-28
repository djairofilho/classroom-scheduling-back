package com.classroomscheduler.service;

import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.repository.EspacoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EspacoService {

    private final EspacoRepository espacoRepository;

    public EspacoService(EspacoRepository espacoRepository) {
        this.espacoRepository = espacoRepository;
    }

    public List<Espaco> listarTodos() {
        return espacoRepository.findAll();
    }

    public List<Espaco> listarDisponiveis() {
        return espacoRepository.findByIndisponivelFalse();
    }

    public List<Espaco> listarPorPredio(Long predioId) {
        return espacoRepository.findByPredioId(predioId);
    }

    public Espaco buscarPorId(Long id) {
        return espacoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Espaco nao encontrado."));
    }

    public Espaco salvar(Espaco espaco) {
        return espacoRepository.save(espaco);
    }

    public Espaco atualizarIndisponibilidade(Long id, boolean indisponivel, String motivo) {
        Espaco espaco = buscarPorId(id);
        espaco.setIndisponivel(indisponivel);
        espaco.setMotivoIndisponibilidade(indisponivel ? motivo : null);
        return espacoRepository.save(espaco);
    }
}
