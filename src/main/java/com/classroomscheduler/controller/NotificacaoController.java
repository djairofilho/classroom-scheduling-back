package com.classroomscheduler.controller;

import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.Notificacao;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.UsuarioRepository;
import com.classroomscheduler.service.NotificacaoService;
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
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoController(NotificacaoService notificacaoService, UsuarioRepository usuarioRepository) {
        this.notificacaoService = notificacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notificacao>> listarTodas() {
        return ResponseEntity.ok(notificacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.buscarPorId(id));
    }

    @GetMapping("/por-destinatario")
    public ResponseEntity<List<Notificacao>> listarPorDestinatario(@RequestParam Long destinatarioId, Authentication authentication) {
        validarAcessoDestinatario(destinatarioId, authentication);
        return ResponseEntity.ok(notificacaoService.listarPorDestinatario(destinatarioId));
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<List<Notificacao>> listarNaoLidas(@RequestParam Long destinatarioId, Authentication authentication) {
        validarAcessoDestinatario(destinatarioId, authentication);
        return ResponseEntity.ok(notificacaoService.listarNaoLidas(destinatarioId));
    }

    @PostMapping
    public ResponseEntity<Notificacao> criar(@RequestBody Notificacao notificacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacaoService.salvar(notificacao));
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<Notificacao> marcarComoLida(@PathVariable Long id, Authentication authentication) {
        Notificacao notificacao = notificacaoService.buscarPorId(id);
        validarAcessoDestinatario(notificacao.getDestinatario().getId(), authentication);
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id));
    }

    private void validarAcessoDestinatario(Long destinatarioId, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (usuario.getPapel() == PapelUsuario.SOLICITANTE && !usuario.getId().equals(destinatarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
