package com.classroomscheduler.controller;

import com.classroomscheduler.dto.CreateReservaRequest;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Reserva>> listarAtivas() {
        return ResponseEntity.ok(reservaService.listarAtivas());
    }

    @GetMapping("/por-solicitante")
    public ResponseEntity<List<Reserva>> listarPorSolicitante(@RequestParam Long solicitanteId) {
        return ResponseEntity.ok(reservaService.listarPorSolicitante(solicitanteId));
    }

    @PostMapping
    public ResponseEntity<Reserva> criar(@RequestBody CreateReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.criar(request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }
}
