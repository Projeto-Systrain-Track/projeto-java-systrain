package school.sptech.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class Jira {
    private static final io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
            .filename(".env.dev")
            .load();

    private final String baseUrl = dotenv.get("JIRA_BASE_URL");
    private final String email = dotenv.get("JIRA_EMAIL");
    private final String apiToken = dotenv.get("JIRA_API_TOKEN");

    private final String authHeader;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public Jira() {
        String auth = email + ":" + apiToken;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    private HttpRequest.Builder baseRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    private String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            return response.body();
        }
        throw new RuntimeException("Jira request failed: " + status + " - " + response.body());
    }

    public String createIssue(String projectKey, String summary, String description, String issueType, String priority, String componente, Integer id_maquina, Integer idEmpresa) throws Exception {
        Map<String, Object> adfDescription = Map.of(
                "type", "doc",
                "version", 1,
                "content", List.of(
                        Map.of(
                                "type", "paragraph",
                                "content", List.of(
                                        Map.of("type", "text", "text", description)
                                )
                        )
                )
        );

        Map<String, Object> payload = Map.of(
                "fields", Map.of(
                        "project", Map.of("key", projectKey),
                        "summary", summary,
                        "description", adfDescription,
                        "issuetype", Map.of("name", issueType),
                        "priority", Map.of("name", priority),
                        "labels", List.of(
                                "incidentes_SysTrainTrack",
                                "id_maquina:" + id_maquina,
                                "componente:" + componente.toUpperCase().replace(" ", "_"),
                                "empresa:" + idEmpresa
                        )
                )
        );

        String json = objectMapper.writeValueAsString(payload);

        HttpRequest request = baseRequest("/rest/api/3/issue")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request);
    }

    public String extractKey(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode keyNode = jsonNode.path("key");

            if (keyNode.isMissingNode()) {
                System.err.println("[Extrair chave] O campo 'key' não foi encontrado no JSON de resposta.");
                return null;
            }
            return keyNode.asText();
        } catch (Exception e) {
            System.err.println("[Extrair chave] Falha crítica ao analisar JSON: " + e.getMessage());
            return null;
        }
    }
}