package com.classroomscheduler.controller;

import com.classroomscheduler.dto.CreateEspacoRequest;
import com.classroomscheduler.dto.UpdateEspacoRequest;
import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.service.EspacoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/espacos", "/spaces"})
public class EspacoController {

    private final EspacoService espacoService;

    public EspacoController(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    @GetMapping
    public ResponseEntity<List<Espaco>> listarTodos(
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Long predioId,
            @RequestParam(required = false, name = "buildingId") Long buildingId
    ) {
        if (Boolean.TRUE.equals(available)) {
            return ResponseEntity.ok(espacoService.listarDisponiveis());
        }
        Long selectedBuildingId = predioId != null ? predioId : buildingId;
        if (selectedBuildingId != null) {
            return ResponseEntity.ok(espacoService.listarPorPredio(selectedBuildingId));
        }
        return ResponseEntity.ok(espacoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Espaco> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(espacoService.buscarPorId(id));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Espaco>> listarDisponiveis() {
        return ResponseEntity.ok(espacoService.listarDisponiveis());
    }

    @GetMapping("/por-predio")
    public ResponseEntity<List<Espaco>> listarPorPredio(@RequestParam Long predioId) {
        return ResponseEntity.ok(espacoService.listarPorPredio(predioId));
    }

    @PostMapping
    public ResponseEntity<Espaco> criar(@RequestBody CreateEspacoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(espacoService.criar(request));
    }

    @PatchMapping({"/{id}/indisponibilidade", "/{id}/availability"})
    public ResponseEntity<Espaco> atualizarIndisponibilidade(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        boolean indisponivel = Boolean.TRUE.equals(body.get("indisponivel"));
        String motivo = body.get("motivo") == null ? null : body.get("motivo").toString();
        return ResponseEntity.ok(espacoService.atualizarIndisponibilidade(id, indisponivel, motivo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Espaco> atualizar(@PathVariable Long id, @RequestBody UpdateEspacoRequest request) {
        return ResponseEntity.ok(espacoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        espacoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
