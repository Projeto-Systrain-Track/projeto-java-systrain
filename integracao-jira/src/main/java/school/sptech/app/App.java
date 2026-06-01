package school.sptech.app;

import school.sptech.controller.JiraController;
import school.sptech.dto.tela.IncidenteTelaDTO;
import school.sptech.service.S3Service;

import java.util.List;

public class App {

    public static void main(String[] args) {
        System.out.println("[Java] Inicializando o sistema de integração de Incidentes...");

        while (true) {
            try {
                System.out.println("------------------------------------------------------------");
                System.out.println("[Java - S3] Buscando dados no S3 e processando incidentes...");
                List<IncidenteTelaDTO> incidentesNovos = S3Service.buscarIncidentesDoS3("client/dashboard_incidentes.json");

                if (incidentesNovos != null && !incidentesNovos.isEmpty()) {
                    System.out.println("[Java - S3] " + incidentesNovos.size() + " incidentes encontrados no arquivo.");

                    JiraController jiraController = new JiraController();

                    for (IncidenteTelaDTO incidente : incidentesNovos) {
                        System.out.println("\n[Java - Fluxo] Iniciando processamento do incidente: " + incidente.detalhes_tela.titulo);

                        //criação do card e recupera a chave
                        String chaveGerada = jiraController.criarChamado(incidente);

                        if (chaveGerada != null) {
                            //guarda a chave p dps a tela saber de qual card é
                            incidente.dados_brutos.jiraKey = chaveGerada;
                        }
                    }

                    System.out.println("\n[Java - Sucesso] Todos os incidentes do ciclo foram integrados ao Jira.");
                } else {
                    System.out.println("[Java - S3] Nenhum incidente pendente no momento.");
                }

            } catch (Exception e) {
                System.out.println("[Java - Erro] Falha no ciclo de processamento: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    //espera 20s para a próxima checagem
                    Thread.sleep(20000);
                } catch (Exception e) {
                    System.out.println("[Java - Erro] Falha no timer: " + e.getMessage());
                }
            }
        }
    }
}