package com.classroomscheduler.service;

import com.classroomscheduler.dto.AuthRequest;
import com.classroomscheduler.dto.AuthResponse;
import com.classroomscheduler.exception.NaoAutorizadoException;
import com.classroomscheduler.exception.RegraDeNegocioException;
import com.classroomscheduler.model.Aluno;
import com.classroomscheduler.model.Funcionario;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.AlunoRepository;
import com.classroomscheduler.repository.FuncionarioRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registrar(AuthRequest request) {
        String email = normalizarEmail(request.getEmail());
        validarSenha(request.getSenha());

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RegraDeNegocioException("Ja existe usuario com esse email.");
        }

        Usuario solicitante = criarSolicitantePorEmail(email);
        solicitante.setEmail(email);
        solicitante.setNome(email);
        solicitante.setSenhaHash(passwordEncoder.encode(request.getSenha()));

        Usuario salvo = salvarSolicitante(solicitante);
        return new AuthResponse(jwtService.gerarToken(salvo), salvo);
    }

    public AuthResponse login(AuthRequest request) {
        String email = normalizarEmail(request.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NaoAutorizadoException("Email ou senha invalidos."));

        if (usuario.getSenhaHash() == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new NaoAutorizadoException("Email ou senha invalidos.");
        }

        return new AuthResponse(jwtService.gerarToken(usuario), usuario);
    }

    public String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraDeNegocioException("Email e obrigatorio.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new RegraDeNegocioException("Senha deve possuir pelo menos 6 caracteres.");
        }
    }

    private TipoSolicitante inferirTipoSolicitante(String email) {
        if (email.endsWith("@al.insper.edu.br")) {
            return TipoSolicitante.ALUNO;
        }

        if (email.endsWith("@insper.edu.br")) {
            return TipoSolicitante.FUNCIONARIO;
        }

        throw new RegraDeNegocioException("Email deve ser institucional do Insper.");
    }

    private Usuario criarSolicitantePorEmail(String email) {
        TipoSolicitante tipoSolicitante = inferirTipoSolicitante(email);

        if (tipoSolicitante == TipoSolicitante.ALUNO) {
            return new Aluno();
        }

        return new Funcionario();
    }

    private Usuario salvarSolicitante(Usuario solicitante) {
        if (solicitante instanceof Aluno aluno) {
            return alunoRepository.save(aluno);
        }

        if (solicitante instanceof Funcionario funcionario) {
            return funcionarioRepository.save(funcionario);
        }

        throw new RegraDeNegocioException("Tipo de solicitante invalido.");
    }
}
