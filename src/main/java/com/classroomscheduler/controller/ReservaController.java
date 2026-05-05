package com.classroomscheduler.controller;

import com.classroomscheduler.dto.CreateReservaRequest;
import com.classroomscheduler.exception.AcessoNegadoException;
import com.classroomscheduler.exception.NaoAutorizadoException;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/reservas", "/reservations"})
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaService reservaService, UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listarTodas(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long solicitanteId,
            @RequestParam(required = false, name = "requesterId") Long requesterId,
            @RequestParam(required = false, name = "spaceId") Long spaceId,
            @RequestParam(required = false, name = "espacoId") Long espacoId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false, name = "data") LocalDate data,
            @RequestParam(required = false) java.time.LocalDateTime from,
            @RequestParam(required = false) java.time.LocalDateTime to,
            Authentication authentication
    ) {
        Long selectedRequesterId = solicitanteId != null ? solicitanteId : requesterId;
        if (selectedRequesterId != null) {
            Usuario usuario = usuarioAutenticado(authentication);
            if (usuario.getPapel() == PapelUsuario.SOLICITANTE && !usuario.getId().equals(selectedRequesterId)) {
                throw new AcessoNegadoException("Usuario nao pode acessar reservas de outro solicitante.");
            }
        }
        Long selectedSpaceId = spaceId != null ? spaceId : espacoId;
        LocalDate selectedDate = date != null ? date : data;
        return ResponseEntity.ok(reservaService.filtrar(status, selectedRequesterId, selectedSpaceId, selectedDate, from, to));
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
            throw new AcessoNegadoException("Usuario nao pode acessar reservas de outro solicitante.");
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

    @PatchMapping({"/{id}/cancelar", "/{id}/cancel"})
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioAutenticado(authentication);
        Reserva reserva = reservaService.buscarPorId(id);
        if (usuario.getPapel() == PapelUsuario.SOLICITANTE && !usuario.getId().equals(reserva.getSolicitante().getId())) {
            throw new AcessoNegadoException("Usuario nao pode cancelar reserva de outro solicitante.");
        }
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    @PatchMapping({"/{id}/aprovar", "/{id}/approve"})
    public ResponseEntity<Reserva> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.aprovar(id));
    }

    @PatchMapping({"/{id}/recusar", "/{id}/reject"})
    public ResponseEntity<Reserva> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.recusar(id));
    }

    @GetMapping("/por-espaco")
    public ResponseEntity<List<Reserva>> listarPorEspacoEData(
            @RequestParam Long espacoId,
            @RequestParam LocalDate data
    ) {
        return ResponseEntity.ok(reservaService.listarPorEspacoEData(espacoId, data));
    }

    private Usuario usuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NaoAutorizadoException("Usuario nao autenticado."));
    }
}
