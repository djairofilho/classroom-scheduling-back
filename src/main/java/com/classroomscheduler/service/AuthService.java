package com.classroomscheduler.service;

import com.classroomscheduler.dto.AuthRequest;
import com.classroomscheduler.dto.AuthResponse;
import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.Solicitante;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.SolicitanteRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SolicitanteRepository solicitanteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            SolicitanteRepository solicitanteRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.solicitanteRepository = solicitanteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registrar(AuthRequest request) {
        String email = normalizarEmail(request.getEmail());
        validarSenha(request.getSenha());

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ja existe usuario com esse email.");
        }

        Solicitante solicitante = new Solicitante();
        solicitante.setEmail(email);
        solicitante.setNome(email);
        solicitante.setPapel(PapelUsuario.SOLICITANTE);
        solicitante.setTipoSolicitante(inferirTipoSolicitante(email));
        solicitante.setSenhaHash(passwordEncoder.encode(request.getSenha()));

        Usuario salvo = solicitanteRepository.save(solicitante);
        return new AuthResponse(jwtService.gerarToken(salvo), salvo);
    }

    public AuthResponse login(AuthRequest request) {
        String email = normalizarEmail(request.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email ou senha invalidos."));

        if (usuario.getSenhaHash() == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new BadCredentialsException("Email ou senha invalidos.");
        }

        return new AuthResponse(jwtService.gerarToken(usuario), usuario);
    }

    public String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email e obrigatorio.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Senha deve possuir pelo menos 6 caracteres.");
        }
    }

    private TipoSolicitante inferirTipoSolicitante(String email) {
        if (email.endsWith("@al.insper.edu.br")) {
            return TipoSolicitante.ALUNO;
        }

        if (email.endsWith("@insper.edu.br")) {
            return TipoSolicitante.FUNCIONARIO;
        }

        throw new IllegalArgumentException("Email deve ser institucional do Insper.");
    }
}
