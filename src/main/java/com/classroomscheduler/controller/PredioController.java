package com.classroomscheduler.controller;

import com.classroomscheduler.model.Predio;
import com.classroomscheduler.service.PredioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/predios")
public class PredioController {

    private final PredioService predioService;

    public PredioController(PredioService predioService) {
        this.predioService = predioService;
    }

    @GetMapping
    public ResponseEntity<List<Predio>> listarTodos() {
        return ResponseEntity.ok(predioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Predio> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(predioService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Predio> buscarPorCodigo(@RequestParam String codigo) {
        return ResponseEntity.ok(predioService.buscarPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<Predio> criar(@RequestBody Predio predio) {
        return ResponseEntity.status(HttpStatus.CREATED).body(predioService.salvar(predio));
    }
}
