import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

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

        String mensagem = "---- RELATÓRIO SEMANAL DA " + json.path(String.valueOf(idEmpresa)).path("linhas").path(String.valueOf(idLinha)).path("nome") + " ----\n\n";
        mensagem += json.path(String.valueOf(idEmpresa)).path("nome")+ "\n";

        System.out.println(mensagem);




    }
}
