package com.classroomscheduler.config;

import com.classroomscheduler.model.Admin;
import com.classroomscheduler.model.Aluno;
import com.classroomscheduler.model.DiaSemana;
import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.model.Funcionario;
import com.classroomscheduler.model.HorarioFuncionamento;
import com.classroomscheduler.model.HorarioReserva;
import com.classroomscheduler.model.Notificacao;
import com.classroomscheduler.model.Predio;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.TipoEspaco;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.EspacoRepository;
import com.classroomscheduler.repository.HorarioFuncionamentoRepository;
import com.classroomscheduler.repository.NotificacaoRepository;
import com.classroomscheduler.repository.PredioRepository;
import com.classroomscheduler.repository.ReservaRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

@Configuration
public class DemoDataConfig {

    @Bean
    public CommandLineRunner demoDataRunner(
            PredioRepository predioRepository,
            EspacoRepository espacoRepository,
            UsuarioRepository usuarioRepository,
            ReservaRepository reservaRepository,
            NotificacaoRepository notificacaoRepository,
            HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            RecursoEspacoRepository recursoEspacoRepository,
            PoliticaReservaRepository politicaReservaRepository,
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

            garantirHorariosPredio(horarioFuncionamentoRepository, predioA);
            garantirHorariosPredio(horarioFuncionamentoRepository, predioB);

            RecursoEspaco projetor = buscarOuCriarRecurso(
                    recursoEspacoRepository,
                    "Projetor",
                    "Projetor multimidia instalado no espaco."
            );
            RecursoEspaco quadro = buscarOuCriarRecurso(
                    recursoEspacoRepository,
                    "Quadro branco",
                    "Quadro branco para aulas e reunioes."
            );
            RecursoEspaco som = buscarOuCriarRecurso(
                    recursoEspacoRepository,
                    "Sistema de som",
                    "Microfones e caixas de som para eventos."
            );

            buscarOuCriarPoliticaReserva(politicaReservaRepository);

            Espaco sala101 = buscarOuCriarEspaco(
                    espacoRepository,
                    predioA,
                    "Sala 101",
                    TipoEspaco.SALA,
                    40,
                    false,
                    null,
                    projetor,
                    quadro
            );

            buscarOuCriarEspaco(
                    espacoRepository,
                    predioA,
                    "Laboratorio Makers",
                    TipoEspaco.LABORATORIO,
                    25,
                    false,
                    null,
                    projetor,
                    quadro
            );

            Espaco auditorioPrincipal = buscarOuCriarEspaco(
                    espacoRepository,
                    predioB,
                    "Auditorio Principal",
                    TipoEspaco.AUDITORIO,
                    120,
                    false,
                    null,
                    projetor,
                    som
            );

            buscarOuCriarEspaco(
                    espacoRepository,
                    predioB,
                    "Quadra Coberta",
                    TipoEspaco.QUADRA,
                    60,
                    true,
                    "Indisponivel para manutencao preventiva"
            );

            garantirHorariosEspaco(horarioFuncionamentoRepository, sala101);
            garantirHorariosEspaco(horarioFuncionamentoRepository, auditorioPrincipal);

            Usuario ana = buscarOuCriarSolicitante(
                    usuarioRepository,
                    "ana.souza@al.insper.edu.br"
            );

            Usuario bruno = buscarOuCriarSolicitante(
                    usuarioRepository,
                    "bruno.lima@insper.edu.br"
            );

            Usuario admin = buscarOuCriarAdmin(
                    usuarioRepository,
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

    private void garantirHorariosPredio(
            HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            Predio predio
    ) {
        for (DiaSemana diaSemana : DiaSemana.values()) {
            LocalTime abertura = diaSemana == DiaSemana.DOMINGO ? LocalTime.of(10, 0) : LocalTime.of(7, 0);
            LocalTime fechamento = diaSemana == DiaSemana.DOMINGO ? LocalTime.of(16, 0) : LocalTime.of(22, 0);
            garantirHorarioPredio(horarioFuncionamentoRepository, predio, diaSemana, abertura, fechamento);
        }
    }

    private void garantirHorarioPredio(
            HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            Predio predio,
            DiaSemana diaSemana,
            LocalTime abertura,
            LocalTime fechamento
    ) {
        boolean existe = horarioFuncionamentoRepository.findByPredioId(predio.getId()).stream()
                .anyMatch(horario -> horario.getDiaSemana() == diaSemana && horario.getEspaco() == null);

        if (existe) {
            return;
        }

        HorarioFuncionamento horario = new HorarioFuncionamento();
        horario.setPredio(predio);
        horario.setDiaSemana(diaSemana);
        horario.setAbertura(abertura);
        horario.setFechamento(fechamento);
        horario.setAtivo(true);
        horarioFuncionamentoRepository.save(horario);
    }

    private void garantirHorariosEspaco(
            HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            Espaco espaco
    ) {
        List<DiaSemana> diasUteis = List.of(
                DiaSemana.SEGUNDA,
                DiaSemana.TERCA,
                DiaSemana.QUARTA,
                DiaSemana.QUINTA,
                DiaSemana.SEXTA
        );

        for (DiaSemana diaSemana : diasUteis) {
            garantirHorarioEspaco(
                    horarioFuncionamentoRepository,
                    espaco,
                    diaSemana,
                    LocalTime.of(8, 0),
                    LocalTime.of(20, 0)
            );
        }
    }

    private void garantirHorarioEspaco(
            HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            Espaco espaco,
            DiaSemana diaSemana,
            LocalTime abertura,
            LocalTime fechamento
    ) {
        boolean existe = horarioFuncionamentoRepository.findByEspacoId(espaco.getId()).stream()
                .anyMatch(horario -> horario.getDiaSemana() == diaSemana);

        if (existe) {
            return;
        }

        HorarioFuncionamento horario = new HorarioFuncionamento();
        horario.setEspaco(espaco);
        horario.setDiaSemana(diaSemana);
        horario.setAbertura(abertura);
        horario.setFechamento(fechamento);
        horario.setAtivo(true);
        horarioFuncionamentoRepository.save(horario);
    }

    private RecursoEspaco buscarOuCriarRecurso(
            RecursoEspacoRepository recursoEspacoRepository,
            String nome,
            String descricao
    ) {
        return recursoEspacoRepository.findByNomeIgnoreCase(nome)
                .orElseGet(() -> {
                    RecursoEspaco recurso = new RecursoEspaco();
                    recurso.setNome(nome);
                    recurso.setDescricao(descricao);
                    return recursoEspacoRepository.save(recurso);
                });
    }

    private PoliticaReserva buscarOuCriarPoliticaReserva(
            PoliticaReservaRepository politicaReservaRepository
    ) {
        return politicaReservaRepository.findByNomeIgnoreCase("Padrao")
                .orElseGet(() -> {
                    PoliticaReserva politica = new PoliticaReserva();
                    politica.setNome("Padrao");
                    politica.setAntecedenciaMinimaHoras(1);
                    politica.setDuracaoMaximaHoras(4);
                    politica.setPermiteFimDeSemana(true);
                    politica.setRequerAprovacaoAdmin(false);
                    return politicaReservaRepository.save(politica);
                });
    }

    private Usuario buscarOuCriarSolicitante(
            UsuarioRepository usuarioRepository,
            String email
    ) {
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        return usuarioRepository.findByEmail(emailNormalizado)
                .orElseGet(() -> {
                    Usuario solicitante = criarSolicitanteDemo(emailNormalizado);
                    solicitante.setNome(emailNormalizado);
                    solicitante.setEmail(emailNormalizado);
                    return usuarioRepository.save(solicitante);
                });
    }

    private Usuario buscarOuCriarAdmin(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            String nome,
            String email,
            String senha
    ) {
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        return usuarioRepository.findByEmail(emailNormalizado)
                .orElseGet(() -> {
                    Usuario admin = new Admin();
                    admin.setNome(nome);
                    admin.setEmail(emailNormalizado);
                    admin.setSenhaHash(passwordEncoder.encode(senha));
                    return usuarioRepository.save(admin);
                });
    }

    private Usuario criarSolicitanteDemo(String emailNormalizado) {
        if (emailNormalizado.endsWith("@al.insper.edu.br")) {
            Aluno aluno = new Aluno();
            aluno.setNumeroMatricula("DEMO-" + Math.abs(emailNormalizado.hashCode()));
            return aluno;
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNumeroCracha("DEMO-" + Math.abs(emailNormalizado.hashCode()));
        funcionario.setCargo("Funcionario");
        return funcionario;
    }

    private Espaco buscarOuCriarEspaco(
            EspacoRepository espacoRepository,
            Predio predio,
            String nome,
            TipoEspaco tipo,
            Integer capacidade,
            boolean indisponivel,
            String motivoIndisponibilidade,
            RecursoEspaco... recursos
    ) {
        return espacoRepository.findByPredioIdAndNomeIgnoreCase(predio.getId(), nome)
                .orElseGet(() -> {
                    Espaco espaco = new Espaco();
                    espaco.setNome(nome);
                    espaco.setTipo(tipo);
                    espaco.setCapacidade(capacidade);
                    espaco.setPredio(predio);
                    espaco.setIndisponivel(indisponivel);
                    espaco.setMotivoIndisponibilidade(motivoIndisponibilidade);
                    espaco.getRecursos().addAll(List.of(recursos));
                    return espacoRepository.save(espaco);
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

        HorarioReserva horarios = new HorarioReserva();
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
