package school.sptech.controller;

import school.sptech.config.Jira;
import school.sptech.dto.tela.IncidenteTelaDTO;

// Imports essenciais e básicos do Java para manipulação de arquivos e listas
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class JiraController {
    private final Jira jira = new Jira();

    private final String HISTORICO_LIDOS = "historico_incidentes.txt";
    private static List<String> chavesEnviadas = new ArrayList<>();

    public JiraController() {
        carregarHistoricoDoArquivo();
    }

    public String criarChamado(IncidenteTelaDTO incidente) {
        try {
            //cria uma assinatura para cada card de incidente lido
            String chaveUnica = incidente.dados_brutos.id_rbc + "_" +
                    incidente.detalhes_tela.componente + "_" +
                    incidente.detalhes_tela.horario + "_" +
                    incidente.dados_brutos.id_empresa;


            if (chavesEnviadas.contains(chaveUnica)) {
                System.out.println("[JiraController] Esse incidente já foi processado - duplicado: " + chaveUnica);
                return null;
            }

            // se passou do if, o incidente é novo
            chavesEnviadas.add(chaveUnica);
            //local onde a chave fica salva caso o card já exista
            salvarChaveNoArquivo(chaveUnica);

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

    private void carregarHistoricoDoArquivo() {
        try {
            File arquivo = new File(HISTORICO_LIDOS);

            // se o arquivo já existir lê ele
            if (arquivo.exists()) {
                Scanner leitor = new Scanner(arquivo);

                while (leitor.hasNextLine()) {
                    String linhaChave = leitor.nextLine();

                    // coloca as chaves antigas de volta na lista da memória RAM
                    if (!chavesEnviadas.contains(linhaChave)) {
                        chavesEnviadas.add(linhaChave);
                    }
                }
                leitor.close();
                System.out.println("[Histórico] Base de guias carregada! Lembrei de " + chavesEnviadas.size() + " incidentes antigos.");
            }
        } catch (Exception e) {
            System.err.println("[Histórico] Erro ao carregar arquivo de guias: " + e.getMessage());
        }
    }

    private void salvarChaveNoArquivo(String chave) {
        try {
            FileWriter escritor = new FileWriter(HISTORICO_LIDOS, true);
            escritor.write(chave + "\n");
            escritor.close();
        } catch (Exception e) {
            System.err.println("[Histórico] Erro ao salvar chave no arquivo local: " + e.getMessage());
        }
    }
}