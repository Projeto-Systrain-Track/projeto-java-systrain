import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        int idEmpresa = 3;
        String nomeEmpresa = "Rota trilhos Seguros";
        int idLinha = 5;
        String nomeLinha = "Linha 7 - Rubi ";
        int idUsuario = 5;
        String emailUsuario = "pedro.holiveira@sptech.school";

        S3DAO s3 = new S3DAO();
        System.out.println("Cliente S3:" + s3);
        String nome_bucket = dotenv.get("AWS_BUCKET");
        List<S3Object> arquivos = s3.listarObjetos(nome_bucket, "client/");
        System.out.println("Arquivos: " + arquivos);
        S3Object arquivo = arquivos.get(1);
        JsonNode json = s3.buscarArquivo(nome_bucket, arquivo.key());
        System.out.println(json.path(String.valueOf(idEmpresa)));
        System.out.println(json.path(String.valueOf(idEmpresa)).path("nome"));


        String nome_linha_formatado = json
                .path(String.valueOf(idEmpresa))
                .path("linhas")
                .path(String.valueOf(idLinha))
                .path("nome")
                .asText()
                .replace(" ", "_")
                .toLowerCase();

        String nome_empresa_formatado = json
                .path(String.valueOf(idEmpresa))
                .path("nome")
                .asText()
                .replace(" ", "_")
                .toLowerCase();
        String data_hora_json = json
                .path(String.valueOf(idEmpresa))
                .path("data_hora")
                .asText();

        Double custo_semanal = json
                .path(String.valueOf(idEmpresa))
                .path("linhas")
                .path(String.valueOf(idLinha))
                .path("resumo")
                .path("custo_opex_desperdicado")
                .asDouble();
        int qte_alertas_semanais = json
                .path(String.valueOf(idEmpresa))
                .path("linhas")
                .path(String.valueOf(idLinha))
                .path("resumo")
                .path("qte_alertas")
                .asInt();
        int qte_alertas_semanais_atencao = json
                .path(String.valueOf(idEmpresa))
                .path("linhas")
                .path(String.valueOf(idLinha))
                .path("resumo")
                .path("tipo_alertas")
                .path("ATENÇÃO")
                .asInt();
        int qte_alertas_semanais_critico = json
                .path(String.valueOf(idEmpresa))
                .path("linhas")
                .path(String.valueOf(idLinha))
                .path("resumo")
                .path("tipo_alertas")
                .path("CRITICO")
                .asInt();
        String nome_relatorio = "relatorio_" + nome_linha_formatado + "_" + nome_empresa_formatado;
        Relatorio relatorio = new Relatorio(nome_relatorio);
        relatorio.abrirRelatorio();
        relatorio.adicionarCabecalho(
                "RELATÓRIO SEMANAL DA " + nomeLinha.toUpperCase(),
                "Baseado na última atualização " + data_hora_json
        );

        JsonNode alertas_por_motivo = json
                .path(String.valueOf(idEmpresa))
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
                    "- Não diga que houve falha real na operação; trate como uma análise baseada em simulação acadêmica.\n" +
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

    }
}
