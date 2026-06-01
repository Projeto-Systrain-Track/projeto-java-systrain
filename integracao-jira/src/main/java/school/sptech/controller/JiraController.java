package school.sptech.controller;

import school.sptech.config.Jira;
import school.sptech.dto.tela.IncidenteTelaDTO;

public class JiraController {
    private final Jira jira = new Jira();

    public String criarChamado(IncidenteTelaDTO incidente) {
        try {
            String tituloJira = "Incidente: " + incidente.detalhes_tela.titulo + " [" + incidente.detalhes_tela.linha + "]";

            String descricaoJira = String.format(
                    "Detalhes do Incidente:\n" +
                            "- Horário: %s\n" +
                            "- Nível de Impacto: %s\n" +
                            "- Motivo do Disparo: %s\n\n" +
                            "Métricas do Servidor no Alerta:\n" +
                            "- CPU: %s\n" +
                            "- RAM: %s\n" +
                            "- DISCO: %s\n" +
                            "- Latência: %s\n\n" +
                            "Contexto Climático Local:\n" +
                            "- Condição: %s (%s°C)\n" +
                            "- Vento: %s",
                    incidente.detalhes_tela.horario,
                    incidente.detalhes_tela.nivel,
                    incidente.metricas_formatadas.disparo,
                    incidente.metricas_formatadas.cpu,
                    incidente.metricas_formatadas.ram,
                    incidente.metricas_formatadas.disco,
                    incidente.metricas_formatadas.latencia,
                    incidente.detalhes_tela.clima.condicao,
                    incidente.detalhes_tela.clima.temperatura,
                    incidente.detalhes_tela.clima.vento_kmh
            );

            String projectKey = "KAN";
            String issueType = "Erro";
            String prioridadeJira = incidente.detalhes_tela.nivel.equalsIgnoreCase("Crítico") ? "Highest" : "Medium";
            String componente = incidente.detalhes_tela.componente;
            Integer idMaquina = incidente.dados_brutos.id_rbc;
            Integer idEmpresa = incidente.dados_brutos.id_empresa;

            System.out.println("[JiraController] Enviando dados para a API do Jira...");

            String responseBody = jira.createIssue(
                    projectKey,
                    tituloJira,
                    descricaoJira,
                    issueType,
                    prioridadeJira,
                    componente,
                    idMaquina,
                    idEmpresa
            );

            String jiraKey = jira.extractKey(responseBody);
            System.out.println("[JiraController] Sucesso! Card criado com a chave: " + jiraKey);

            return jiraKey;

        } catch (Exception e) {
            System.err.println("[JiraController] Erro ao integrar com a API do Jira: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}