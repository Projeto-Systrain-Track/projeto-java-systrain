import com.fasterxml.jackson.databind.JsonNode;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        String idEmpresa = args[0];
        String nomeEmpresa = args[1];
        String nomeEmpresaFormatado = nomeEmpresa.trim().replaceAll(" ", "_").toLowerCase();
        String idLinha = args[2];
        String nomeLinha = args[3];
        String emailUsuario = args[4];
        String caminho = args[5];
        String caminhoEnv = args[6];
        S3DAO s3 = new S3DAO();
        System.out.println("Cliente S3:" + s3);
        String nome_bucket = System.getenv("S3_BUCKET_NAME");
        String caminho_client = "client/" + nomeEmpresaFormatado + "/";
        List<S3Object> arquivos = s3.listarObjetos(nome_bucket, caminho_client);
        boolean gerouRelatorio = false;
        if (arquivos != null) {
            System.out.printf("Arquivos: " + arquivos);
            for (S3Object arquivo : arquivos) {
                System.out.println("FOR ARQUIVO: " + arquivo);
                if (arquivo.key().endsWith("dashboard_operacao.json")) {
                    JsonNode json = s3.buscarArquivo(nome_bucket, arquivo.key());
                    System.out.println(String.valueOf(json));
                    String nome_linha_formatado = json
                            .path("linhas")
                            .path(String.valueOf(idLinha))
                            .path("nome")
                            .asText()
                            .replace(" ", "_")
                            .toLowerCase();

                    String nome_empresa_formatado = json
                            .path("nome")
                            .asText()
                            .replace(" ", "_")
                            .toLowerCase();
                    String data_hora_json = json
                            .path("data_hora")
                            .asText();

                    Double custo_semanal = json
                            .path("linhas")
                            .path(String.valueOf(idLinha))
                            .path("resumo")
                            .path("custo_opex_desperdicado")
                            .asDouble();
                    int qte_alertas_semanais = json
                            .path("linhas")
                            .path(String.valueOf(idLinha))
                            .path("resumo")
                            .path("qte_alertas")
                            .asInt();
                    int qte_alertas_semanais_atencao = json
                            .path("linhas")
                            .path(String.valueOf(idLinha))
                            .path("resumo")
                            .path("tipo_alertas")
                            .path("ATENÇÃO")
                            .asInt();
                    int qte_alertas_semanais_critico = json
                            .path("linhas")
                            .path(String.valueOf(idLinha))
                            .path("resumo")
                            .path("tipo_alertas")
                            .path("CRITICO")
                            .asInt();
                    Relatorio relatorio = new Relatorio(caminho);
                    relatorio.abrirRelatorio();
                    relatorio.adicionarCabecalho(
                            "RELATÓRIO SEMANAL DA " + nomeLinha.toUpperCase(),
                            "Baseado na última atualização " + data_hora_json
                    );

                    JsonNode alertas_por_motivo = json
                            .path("linhas")
                            .path(String.valueOf(idLinha))
                            .path("resumo")
                            .path("alertas_por_motivo");
                    Iterator<Map.Entry<String, JsonNode>> campos = alertas_por_motivo.fields();
                    String motivos = "";
                    while (campos.hasNext()) {
                        Map.Entry<String, JsonNode> campo = campos.next();
                        String motivo = campo.getKey();
                        JsonNode valor = campo.getValue();
                        motivos += motivo + ": " + valor + "\n";
                    }
                    Gemini agente = new Gemini();


                    String prompt =
                            "Você é um analista técnico de operação ferroviária especializado em monitoramento de RBCs.\n" +
                                    "O RBC é um servidor crítico monitorado pelo sistema SysTrainTrack. O objetivo deste relatório é ajudar o usuário a identificar quais problemas ocorreram na semana, quais componentes foram mais afetados e quais ações devem ser priorizadas.\n" +
                                    "\n" +
                                    "Com base exclusivamente nos dados abaixo, gere uma análise curta, técnica e objetiva para um relatório semanal.\n" +
                                    "\n" +
                                    "Regras obrigatórias:\n" +
                                    "- Não invente dados.\n" +
                                    "- Não crie causas que não estejam evidentes nos dados.\n" +
                                    "- Não diga que houve falha real na operação;\n" +
                                    "- Não use markdown, não use #, não use ** e não use emojis.\n" +
                                    "- Escreva apenas os tópicos solicitados.\n" +
                                    "- Use linguagem profissional, clara e direta.\n" +
                                    "- Foque em identificar problemas nos RBCs, impacto operacional e prioridade de investigação.\n" +
                                    "- Quando houver alertas críticos, destaque que eles devem ter maior prioridade.\n" +
                                    "- Quando houver muitos alertas de atenção, indique acompanhamento preventivo.\n" +
                                    "- Quando citar valores financeiros, trate como OPEX desperdiçado estimado.\n" +
                                    "Divida a resposta exatamente nos seguintes tópicos:\n" +
                                    "1. Resumo executivo\n" +
                                    "2. Diagnóstico da semana\n" +
                                    "3. Problemas identificados nos RBCs\n" +
                                    "4. Pontos de atenção\n" +
                                    "5. Recomendações\n" +
                                    "Dados do relatório:\n" +
                                    "Empresa: " + nomeEmpresa + "\n" +
                                    "Linha: " + nomeLinha + "\n" +
                                    "OPEX desperdiçado estimado na semana: R$ " + custo_semanal + "\n" +
                                    "Total de alertas na semana: " + qte_alertas_semanais + "\n" +
                                    "Alertas de atenção: " + qte_alertas_semanais_atencao + "\n" +
                                    "Alertas críticos: " + qte_alertas_semanais_critico + "\n" +
                                    "Motivos dos alertas:\n" +
                                    motivos + "\n" +
                                    "Com base nesses dados, gere uma análise que ajude o usuário a entender quais problemas estão afetando os RBCs e quais ações devem ser priorizadas.";
                    String resposta = agente.gerarAnaliseRelatorio(prompt);
                    if (resposta != null) {
                        relatorio.escreverCorpo(resposta);
                    }
                    relatorio.fecharRelatorio();
                    gerouRelatorio = true;
                }
            }
        }

        if (!gerouRelatorio) {
            String prompt =
                    "Você é um analista técnico de operação ferroviária especializado em monitoramento de RBCs.\n" +
                            "O RBC é um servidor crítico monitorado pelo sistema SysTrainTrack.\n" +
                            "\n" +
                            "Regras obrigatórias:\n" +
                            "- Não invente dados.\n" +
                            "- Não crie causas que não estejam evidentes nos dados.\n" +
                            "- Não use markdown, não use #, não use ** e não use emojis.\n" +
                            "- Escreva apenas os tópicos solicitados.\n" +
                            "- Use linguagem profissional, clara e direta.\n" +
                            "\n" +
                            "Contexto da situação:\n" +
                            "O sistema SysTrainTrack realiza coletas periódicas de dados dos RBCs de cada linha ferroviária.\n" +
                            "Essas coletas são feitas por um script de captura que roda continuamente no servidor da linha.\n" +
                            "Quando nenhuma coleta é encontrada para uma linha em um determinado período, isso pode indicar:\n" +
                            "- O script de captura parou de executar ou travou no servidor.\n" +
                            "- Houve queda de conexão entre o servidor da linha e o sistema.\n" +
                            "- O script foi interrompido manualmente ou por falha no ambiente.\n" +
                            "- O servidor da linha está offline ou sem recursos suficientes para manter o script ativo.\n" +
                            "\n" +
                            "Divida a resposta exatamente nos seguintes tópicos:\n" +
                            "1. Resumo executivo\n" +
                            "2. Diagnóstico da semana\n" +
                            "3. Problemas identificados nos RBCs\n" +
                            "4. Pontos de atenção\n" +
                            "5. Recomendações\n" +
                            "\n" +
                            "Dados do relatório:\n" +
                            "Empresa: " + nomeEmpresa + "\n" +
                            "Linha: " + nomeLinha + "\n" +
                            "Situação: Nenhuma coleta de dados foi encontrada para esta linha no período analisado.\n" +
                            "\n" +
                            "Com base nessa situação, redija uma análise técnica informando que não foram localizadas coletas " +
                            "de dados da linha " + nomeLinha + " da empresa " + nomeEmpresa + " nesta semana. " +
                            "Destaque que a ausência de coletas impede qualquer avaliação do estado dos RBCs e representa " +
                            "um risco operacional, pois falhas e anomalias podem estar ocorrendo sem registro ou visibilidade. " +
                            "Oriente a equipe técnica a verificar imediatamente se o script de captura está em execução no servidor " +
                            "da linha, se o processo não foi interrompido, se há erros de conexão ou falhas no ambiente de execução, " +
                            "e se o servidor dispõe de recursos suficientes para manter o script ativo. " +
                            "Recomende também que seja implementado um mecanismo de monitoramento do próprio script, como alertas " +
                            "automáticos em caso de inatividade, para evitar semanas sem coleta no futuro.";

            Gemini agente = new Gemini();
            String resposta = agente.gerarAnaliseRelatorio(prompt);

            Relatorio relatorio = new Relatorio(caminho);
            relatorio.abrirRelatorio();
            relatorio.adicionarCabecalho(
                    "RELATÓRIO SEMANAL DA " + nomeLinha.toUpperCase(),
                    "Baseado na última atualização " + LocalDate.now()
            );
            if (resposta != null) {
                relatorio.escreverCorpo(resposta);
            }
            relatorio.fecharRelatorio();
        }

    }
}
