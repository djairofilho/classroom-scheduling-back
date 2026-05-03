package com.classroomscheduler.service;

import com.classroomscheduler.exception.RecursoNaoEncontradoException;
import com.classroomscheduler.model.Predio;
import com.classroomscheduler.repository.PredioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PredioService {

    private final PredioRepository predioRepository;

    public PredioService(PredioRepository predioRepository) {
        this.predioRepository = predioRepository;
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
}
