package com.classroomscheduler.config;

import com.classroomscheduler.model.Admin;
import com.classroomscheduler.model.Auditorio;
import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.model.Horarios;
import com.classroomscheduler.model.Laboratorio;
import com.classroomscheduler.model.Notificacao;
import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.Predio;
import com.classroomscheduler.model.Quadra;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.Sala;
import com.classroomscheduler.model.Solicitante;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.AdminRepository;
import com.classroomscheduler.repository.EspacoRepository;
import com.classroomscheduler.repository.NotificacaoRepository;
import com.classroomscheduler.repository.PredioRepository;
import com.classroomscheduler.repository.ReservaRepository;
import com.classroomscheduler.repository.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Locale;

@Configuration
public class DemoDataConfig {

    @Bean
    public CommandLineRunner demoDataRunner(
            PredioRepository predioRepository,
            EspacoRepository espacoRepository,
            AdminRepository adminRepository,
            SolicitanteRepository solicitanteRepository,
            ReservaRepository reservaRepository,
            NotificacaoRepository notificacaoRepository,
            PasswordEncoder passwordEncoder,
            @Value("${APP_ADMIN_NOME:Administrador Padrao}") String adminNome,
            @Value("${APP_ADMIN_EMAIL:admin@insper.edu.br}") String adminEmail,
            @Value("${APP_ADMIN_PASSWORD:admin1234}") String adminPassword,
            @Value("${APP_DEMO_DATA_ENABLED:true}") boolean demoDataEnabled
    ) {
        return args -> {
            if (!demoDataEnabled) {
                return;
            }

            Predio predioA = buscarOuCriarPredio(
                    predioRepository,
                    "BL-A",
                    "Predio Academico A",
                    "Rua do Campus, 100"
            );

            Predio predioB = buscarOuCriarPredio(
                    predioRepository,
                    "BL-B",
                    "Predio Academico B",
                    "Rua do Campus, 200"
            );

            Espaco sala101 = buscarOuCriarEspaco(
                    espacoRepository,
                    predioA,
                    "Sala 101",
                    40,
                    new Sala(),
                    false,
                    null
            );

            buscarOuCriarEspaco(
                    espacoRepository,
                    predioA,
                    "Laboratorio Makers",
                    25,
                    new Laboratorio(),
                    false,
                    null
            );

            Espaco auditorioPrincipal = buscarOuCriarEspaco(
                    espacoRepository,
                    predioB,
                    "Auditorio Principal",
                    120,
                    new Auditorio(),
                    false,
                    null
            );

            buscarOuCriarEspaco(
                    espacoRepository,
                    predioB,
                    "Quadra Coberta",
                    60,
                    new Quadra(),
                    true,
                    "Indisponivel para manutencao preventiva"
            );

            Solicitante ana = buscarOuCriarSolicitante(
                    solicitanteRepository,
                    "ana.souza@al.insper.edu.br"
            );

            Solicitante bruno = buscarOuCriarSolicitante(
                    solicitanteRepository,
                    "bruno.lima@insper.edu.br"
            );

            Admin admin = buscarOuCriarAdmin(
                    adminRepository,
                    passwordEncoder,
                    adminNome,
                    adminEmail,
                    adminPassword
            );

            Reserva reservaDemo = buscarOuCriarReserva(
                    reservaRepository,
                    ana,
                    sala101,
                    "Apresentacao de projeto integrador"
            );

            Reserva reservaAdmin = buscarOuCriarReserva(
                    reservaRepository,
                    admin,
                    auditorioPrincipal,
                    "Revisao administrativa da agenda"
            );

            buscarOuCriarNotificacao(
                    notificacaoRepository,
                    ana,
                    reservaDemo,
                    "Sua reserva de demonstracao foi criada com sucesso."
            );

            buscarOuCriarNotificacao(
                    notificacaoRepository,
                    bruno,
                    null,
                    "Voce ja pode explorar a API usando os dados de demonstracao."
            );

            buscarOuCriarNotificacao(
                    notificacaoRepository,
                    admin,
                    reservaAdmin,
                    "Reserva administrativa de demonstracao criada para o painel."
            );
        };
    }

    private Predio buscarOuCriarPredio(
            PredioRepository predioRepository,
            String codigo,
            String nome,
            String localizacao
    ) {
        return predioRepository.findByCodigo(codigo)
                .orElseGet(() -> {
                    Predio predio = new Predio();
                    predio.setCodigo(codigo);
                    predio.setNome(nome);
                    predio.setLocalizacao(localizacao);
                    return predioRepository.save(predio);
                });
    }

    private Solicitante buscarOuCriarSolicitante(
            SolicitanteRepository solicitanteRepository,
            String email
    ) {
        return solicitanteRepository.findByEmail(email)
                .orElseGet(() -> {
                    Solicitante solicitante = new Solicitante();
                    solicitante.setNome(email);
                    solicitante.setEmail(email);
                    solicitante.setPapel(PapelUsuario.SOLICITANTE);
                    solicitante.setTipoSolicitante(
                            email.endsWith("@al.insper.edu.br")
                                    ? TipoSolicitante.ALUNO
                                    : TipoSolicitante.FUNCIONARIO
                    );
                    return solicitanteRepository.save(solicitante);
                });
    }

    private Admin buscarOuCriarAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            String nome,
            String email,
            String senha
    ) {
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        return adminRepository.findByEmail(emailNormalizado)
                .orElseGet(() -> {
                    Admin admin = new Admin();
                    admin.setNome(nome);
                    admin.setEmail(emailNormalizado);
                    admin.setPapel(PapelUsuario.ADMIN);
                    admin.setSenhaHash(passwordEncoder.encode(senha));
                    return adminRepository.save(admin);
                });
    }

    private Espaco buscarOuCriarEspaco(
            EspacoRepository espacoRepository,
            Predio predio,
            String nome,
            Integer capacidade,
            Espaco novoEspaco,
            boolean indisponivel,
            String motivoIndisponibilidade
    ) {
        return espacoRepository.findByPredioIdAndNomeIgnoreCase(predio.getId(), nome)
                .orElseGet(() -> {
                    novoEspaco.setNome(nome);
                    novoEspaco.setCapacidade(capacidade);
                    novoEspaco.setPredio(predio);
                    novoEspaco.setIndisponivel(indisponivel);
                    novoEspaco.setMotivoIndisponibilidade(motivoIndisponibilidade);
                    return espacoRepository.save(novoEspaco);
                });
    }

    private Reserva buscarOuCriarReserva(
            ReservaRepository reservaRepository,
            Usuario solicitante,
            Espaco espaco,
            String motivo
    ) {
        boolean existeReserva = reservaRepository.existsBySolicitanteIdAndEspacoIdAndMotivo(
                solicitante.getId(),
                espaco.getId(),
                motivo
        );

        if (existeReserva) {
            return reservaRepository.findFirstBySolicitanteIdAndEspacoIdAndMotivo(
                    solicitante.getId(),
                    espaco.getId(),
                    motivo
            ).orElseThrow();
        }

        Horarios horarios = new Horarios();
        LocalDateTime inicio = LocalDateTime.now()
                .plusDays(1)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        horarios.setInicio(inicio);
        horarios.setFim(inicio.plusHours(2));

        Reserva reserva = new Reserva();
        reserva.setSolicitante(solicitante);
        reserva.setEspaco(espaco);
        reserva.setHorarios(horarios);
        reserva.setMotivo(motivo);
        reserva.setCancelada(false);
        return reservaRepository.save(reserva);
    }

    private void buscarOuCriarNotificacao(
            NotificacaoRepository notificacaoRepository,
            Usuario destinatario,
            Reserva reserva,
            String mensagem
    ) {
        boolean existeNotificacao = notificacaoRepository.existsByDestinatarioIdAndMensagem(
                destinatario.getId(),
                mensagem
        );

        if (existeNotificacao) {
            return;
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setDestinatario(destinatario);
        notificacao.setReserva(reserva);
        notificacao.setMensagem(mensagem);
        notificacao.setLida(false);
        notificacaoRepository.save(notificacao);
    }
}
