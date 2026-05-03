package com.classroomscheduler.controller;

import com.classroomscheduler.dto.CreateReservaRequest;
import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.UsuarioRepository;
import com.classroomscheduler.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaService reservaService, UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
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
    public ResponseEntity<List<Reserva>> listarPorSolicitante(@RequestParam Long solicitanteId, Authentication authentication) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (usuario.getPapel() == PapelUsuario.SOLICITANTE && !usuario.getId().equals(solicitanteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(reservaService.listarPorSolicitante(solicitanteId));
    }

    @PostMapping
    public ResponseEntity<Reserva> criar(@RequestBody CreateReservaRequest request, Authentication authentication) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (usuario.getPapel() == PapelUsuario.SOLICITANTE) {
            request.setSolicitanteId(usuario.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.criar(request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioAutenticado(authentication);
        Reserva reserva = reservaService.buscarPorId(id);
        if (usuario.getPapel() == PapelUsuario.SOLICITANTE && !usuario.getId().equals(reserva.getSolicitante().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    private Usuario usuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
