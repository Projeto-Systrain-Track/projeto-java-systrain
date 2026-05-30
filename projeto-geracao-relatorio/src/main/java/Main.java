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
        String nome_relatorio = "relatorio_" + nome_linha_formatado + "_" + nome_empresa_formatado;
        Relatorio relatorio = new Relatorio(nome_relatorio);
        relatorio.abrirRelatorio();
        relatorio.adicionarCabecalho(
                "RELATÓRIO SEMANAL DA " + nomeLinha.toUpperCase(),
                "Baseado na última atualização " + data_hora_json
        );

        relatorio.escreverCorpo("Custo OPEX desperdicado: " + json
                .path(String.valueOf(idEmpresa))
                .path("linhas")
                .path(String.valueOf(idLinha))
                .path("resumo")
                .path("custo_opex_desperdicado")
                .asDouble());
        relatorio.escreverCorpo(
                "Quantidade de alertas: " + json
                        .path(String.valueOf(idEmpresa))
                        .path("linhas")
                        .path(String.valueOf(idLinha))
                        .path("resumo")
                        .path("qte_alertas")
                        .asInt()
        );
        relatorio.escreverCorpo("Quantidade de alertas por tipo: ");
        relatorio.escreverCorpo(
                "  - ATENÇÃO: " + json
                        .path(String.valueOf(idEmpresa))
                        .path("linhas")
                        .path(String.valueOf(idLinha))
                        .path("resumo")
                        .path("tipo_alertas")
                        .path("ATENÇÃO")
                        .asInt()
        );
        relatorio.escreverCorpo(
                "  - CRÍTICO: " + json
                        .path(String.valueOf(idEmpresa))
                        .path("linhas")
                        .path(String.valueOf(idLinha))
                        .path("resumo")
                        .path("tipo_alertas")
                        .path("CRITICO")
                        .asInt()
        );
        relatorio.escreverCorpo("Quantidade de alertas por motivo: ");

        JsonNode alertas_por_motivo = json
                .path(String.valueOf(idEmpresa))
                .path("linhas")
                .path(String.valueOf(idLinha))
                .path("resumo")
                .path("alertas_por_motivo");
        Iterator<Map.Entry<String, JsonNode>> campos = alertas_por_motivo.fields();

        while(campos.hasNext()){
            Map.Entry<String, JsonNode> campo = campos.next();
            String motivo = campo.getKey();
            JsonNode valor = campo.getValue();
            relatorio.escreverCorpo(
                    "   - " + motivo + ": " + valor
            );
            System.out.println("Motivo: " + motivo);
            System.out.println("Valor: " + valor);
        }

        relatorio.fecharRelatorio();

    }
}
