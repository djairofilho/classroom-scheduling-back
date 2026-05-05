package com.classroomscheduler;

import com.classroomscheduler.model.Espaco;
import com.classroomscheduler.model.HorarioReserva;
import com.classroomscheduler.model.PapelUsuario;
import com.classroomscheduler.model.Predio;
import com.classroomscheduler.model.Reserva;
import com.classroomscheduler.model.StatusReserva;
import com.classroomscheduler.model.TipoEspaco;
import com.classroomscheduler.model.TipoSolicitante;
import com.classroomscheduler.model.Usuario;
import com.classroomscheduler.repository.EspacoRepository;
import com.classroomscheduler.repository.NotificacaoRepository;
import com.classroomscheduler.repository.PredioRepository;
import com.classroomscheduler.repository.ReservaRepository;
import com.classroomscheduler.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "APP_DEMO_DATA_ENABLED=false"
)
class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PredioRepository predioRepository;

    @Autowired
    private EspacoRepository espacoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String adminToken;
    private String userToken;
    private Usuario user;

    @BeforeEach
    void setup() throws Exception {
        notificacaoRepository.deleteAll();
        reservaRepository.deleteAll();
        espacoRepository.deleteAll();
        predioRepository.deleteAll();
        usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getPapel() != PapelUsuario.ADMIN)
                .forEach(usuarioRepository::delete);

        adminToken = autenticarAdmin();
        userToken = registrarUsuario("user" + System.nanoTime() + "@al.insper.edu.br", "senha123");
        user = usuarioRepository.findByEmail(extrairEmailDoToken(userToken)).orElseThrow();
        assertNotNull(adminToken);
        assertNotNull(userToken);
    }

    @Test
    void userAutenticadoAcessaPorEspacoComSucesso() throws Exception {
        Predio predio = criarPredio("A", "Predio A");
        Espaco espaco = criarEspaco(predio, "Sala 1", TipoEspaco.SALA);
        LocalDate data = LocalDate.of(2026, 5, 10);
        reservaRepository.save(criarReserva(user, espaco, data.atTime(8, 0), data.atTime(9, 0), StatusReserva.PENDENTE, false));

        HttpResponse<String> response = get(userToken, "/reservas/por-espaco?espacoId=" + espaco.getId() + "&data=" + data);
        assertEquals(200, response.statusCode());
    }

    @Test
    void porEspacoFiltraDiaEEspacoCorretamente() throws Exception {
        Predio predio = criarPredio("A", "Predio A");
        Espaco espacoA = criarEspaco(predio, "Sala A", TipoEspaco.SALA);
        Espaco espacoB = criarEspaco(predio, "Sala B", TipoEspaco.SALA);
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalDate outraData = data.plusDays(1);

        reservaRepository.save(criarReserva(user, espacoA, data.atTime(8, 0), data.atTime(9, 0), StatusReserva.PENDENTE, false));
        reservaRepository.save(criarReserva(user, espacoA, outraData.atTime(8, 0), outraData.atTime(9, 0), StatusReserva.PENDENTE, false));
        reservaRepository.save(criarReserva(user, espacoB, data.atTime(8, 0), data.atTime(9, 0), StatusReserva.PENDENTE, false));

        HttpResponse<String> response = get(userToken, "/reservas/por-espaco?espacoId=" + espacoA.getId() + "&data=" + data);
        JsonNode json = objectMapper.readTree(response.body());

        assertEquals(200, response.statusCode());
        assertEquals(1, json.size());
        assertEquals(espacoA.getId(), json.get(0).get("espaco").get("id").asLong());
        assertEquals("2026-05-10T08:00:00", json.get(0).get("horarios").get("inicio").asText());
    }

    @Test
    void recusadaECanceladaNaoEntramComoOcupacao() throws Exception {
        Predio predio = criarPredio("A", "Predio A");
        Espaco espaco = criarEspaco(predio, "Sala 1", TipoEspaco.SALA);
        LocalDate data = LocalDate.of(2026, 5, 10);

        reservaRepository.save(criarReserva(user, espaco, data.atTime(8, 0), data.atTime(9, 0), StatusReserva.PENDENTE, false));
        reservaRepository.save(criarReserva(user, espaco, data.atTime(10, 0), data.atTime(11, 0), StatusReserva.APROVADA, false));
        reservaRepository.save(criarReserva(user, espaco, data.atTime(12, 0), data.atTime(13, 0), StatusReserva.RECUSADA, true));
        reservaRepository.save(criarReserva(user, espaco, data.atTime(14, 0), data.atTime(15, 0), StatusReserva.CANCELADA, true));

        HttpResponse<String> response = get(userToken, "/reservas/por-espaco?espacoId=" + espaco.getId() + "&data=" + data);
        JsonNode json = objectMapper.readTree(response.body());

        assertEquals(200, response.statusCode());
        assertEquals(2, json.size());
    }

    @Test
    void postReservasCriaStatusPendente() throws Exception {
        Predio predio = criarPredio("A", "Predio A");
        Espaco espaco = criarEspaco(predio, "Sala 1", TipoEspaco.SALA);

        String body = """
                {
                  "solicitanteId": 9999,
                  "espacoId": %d,
                  "inicio": "2026-05-10T10:00:00",
                  "fim": "2026-05-10T11:00:00",
                  "motivo": "Monitoria"
                }
                """.formatted(espaco.getId());

        HttpResponse<String> response = send("POST", "/reservas", userToken, body);
        JsonNode json = objectMapper.readTree(response.body());

        assertEquals(201, response.statusCode());
        assertEquals("PENDENTE", json.get("status").asText());
        assertEquals(false, json.get("cancelada").asBoolean());
        assertEquals(user.getId(), json.get("solicitante").get("id").asLong());
    }

    @Test
    void patchAprovarERecusarAtualizamStatus() throws Exception {
        Predio predio = criarPredio("A", "Predio A");
        Espaco espaco = criarEspaco(predio, "Sala 1", TipoEspaco.SALA);
        Reserva reserva = reservaRepository.save(criarReserva(
                user, espaco, LocalDateTime.of(2026, 5, 10, 9, 0), LocalDateTime.of(2026, 5, 10, 10, 0),
                StatusReserva.PENDENTE, false
        ));

        HttpResponse<String> aprovar = send("PATCH", "/reservas/" + reserva.getId() + "/aprovar", adminToken, "");
        JsonNode aprovado = objectMapper.readTree(aprovar.body());
        assertEquals(200, aprovar.statusCode());
        assertEquals("APROVADA", aprovado.get("status").asText());

        HttpResponse<String> recusar = send("PATCH", "/reservas/" + reserva.getId() + "/recusar", adminToken, "");
        JsonNode recusado = objectMapper.readTree(recusar.body());
        assertEquals(200, recusar.statusCode());
        assertEquals("RECUSADA", recusado.get("status").asText());
        assertEquals(true, recusado.get("cancelada").asBoolean());
    }

    @Test
    void aprovacaoComConflitoRetorna409() throws Exception {
        Predio predio = criarPredio("A", "Predio A");
        Espaco espaco = criarEspaco(predio, "Sala 1", TipoEspaco.SALA);

        Reserva aprovada = reservaRepository.save(criarReserva(
                user, espaco, LocalDateTime.of(2026, 5, 10, 9, 0), LocalDateTime.of(2026, 5, 10, 10, 0),
                StatusReserva.APROVADA, false
        ));
        Reserva pendente = reservaRepository.save(criarReserva(
                user, espaco, LocalDateTime.of(2026, 5, 10, 9, 30), LocalDateTime.of(2026, 5, 10, 10, 30),
                StatusReserva.PENDENTE, false
        ));

        HttpResponse<String> response = send("PATCH", "/reservas/" + pendente.getId() + "/aprovar", adminToken, "");
        JsonNode json = objectMapper.readTree(response.body());

        assertNotNull(aprovada.getId());
        assertEquals(409, response.statusCode());
        assertEquals("Conflict", json.get("error").asText());
    }

    @Test
    void registerNaoCriaAdminMesmoComPayloadMalicioso() throws Exception {
        String email = "malicioso" + System.nanoTime() + "@al.insper.edu.br";
        String body = """
                {
                  "email": "%s",
                  "senha": "senha123",
                  "papel": "ADMIN"
                }
                """.formatted(email);

        HttpResponse<String> response = send("POST", "/auth/register", null, body);
        JsonNode json = objectMapper.readTree(response.body());

        assertEquals(201, response.statusCode());
        assertNotEquals("ADMIN", json.get("usuario").get("papel").asText());
    }

    @Test
    void listarAtivasFuncionaParaUser() throws Exception {
        HttpResponse<String> response = get(userToken, "/reservas/ativas");
        assertEquals(200, response.statusCode());
    }

    private Predio criarPredio(String codigo, String nome) {
        Predio predio = new Predio();
        predio.setCodigo(codigo);
        predio.setNome(nome);
        predio.setLocalizacao("Campus");
        return predioRepository.save(predio);
    }

    private Espaco criarEspaco(Predio predio, String nome, TipoEspaco tipo) {
        Espaco espaco = new Espaco();
        espaco.setNome(nome);
        espaco.setCapacidade(40);
        espaco.setTipo(tipo);
        espaco.setPredio(predio);
        espaco.setIndisponivel(false);
        return espacoRepository.save(espaco);
    }

    private Reserva criarReserva(
            Usuario solicitante,
            Espaco espaco,
            LocalDateTime inicio,
            LocalDateTime fim,
            StatusReserva status,
            boolean cancelada
    ) {
        HorarioReserva horarios = new HorarioReserva();
        horarios.setInicio(inicio);
        horarios.setFim(fim);

        Reserva reserva = new Reserva();
        reserva.setSolicitante(solicitante);
        reserva.setEspaco(espaco);
        reserva.setHorarios(horarios);
        reserva.setMotivo("Teste");
        reserva.setStatus(status);
        reserva.setCancelada(cancelada);
        return reserva;
    }

    private HttpResponse<String> get(String token, String path) throws Exception {
        return send("GET", path, token, null);
    }

    private HttpResponse<String> send(String method, String path, String token, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url(path)));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        if (body != null) {
            builder.header("Content-Type", "application/json");
        }

        HttpRequest request = switch (method) {
            case "GET" -> builder.GET().build();
            case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body)).build();
            case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(body == null ? "" : body)).build();
            case "PATCH" -> builder.method("PATCH", HttpRequest.BodyPublishers.ofString(body == null ? "" : body)).build();
            case "DELETE" -> builder.DELETE().build();
            default -> throw new IllegalArgumentException("Metodo nao suportado: " + method);
        };

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String autenticarAdmin() throws Exception {
        String body = """
                {
                  "email": "admin@insper.edu.br",
                  "senha": "admin1234"
                }
                """;
        HttpResponse<String> response = send("POST", "/auth/login", null, body);
        if (response.statusCode() != 200) {
            return null;
        }
        return objectMapper.readTree(response.body()).get("token").asText();
    }

    private String registrarUsuario(String email, String senha) throws Exception {
        String body = """
                {
                  "email": "%s",
                  "senha": "%s"
                }
                """.formatted(email, senha);
        HttpResponse<String> response = send("POST", "/auth/register", null, body);
        if (response.statusCode() != 201) {
            throw new IllegalStateException("Falha ao registrar usuario de teste: " + response.body());
        }
        return objectMapper.readTree(response.body()).get("token").asText();
    }

    private String extrairEmailDoToken(String token) throws Exception {
        JsonNode me = objectMapper.readTree(get(token, "/auth/me").body());
        return me.get("email").asText();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
