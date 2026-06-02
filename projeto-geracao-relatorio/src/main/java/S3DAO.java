import io.github.cdimascio.dotenv.Dotenv;
import kotlin.jvm.internal.TypeReference;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class S3DAO {
    public List<S3Object> listarObjetos (String nome_bucket, String caminho){
        Dotenv dotenv = Dotenv.load();
        final S3Client s3 = ConexaoS3.criarCliente();

        try{
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(nome_bucket)
                    .prefix(caminho)
                    .build();
            ListObjectsV2Response listResponse = s3.listObjectsV2(listRequest);
            System.out.println("listResquest: " + listRequest);
            System.out.println("listResponse: " + listResponse);
            List<S3Object> arquivos = listResponse.contents();
            System.out.println("arquivos: " + arquivos);
            System.out.println("Listar objetos feito com sucesso!");
            return arquivos;
        } catch (Exception e) {
            System.out.println("ERRO AO LISTAR OBJETOS");
            return null;
        }
    }

    public JsonNode buscarArquivo(String nome_bucket, String caminho_arquivo){
        S3Client s3 = ConexaoS3.criarCliente();
        GetObjectRequest requisicao = GetObjectRequest.builder()
                .bucket(nome_bucket)
                .key(caminho_arquivo)
                .build();
        ResponseInputStream<GetObjectResponse> arquivo = s3.getObject(requisicao);
        String conteudo = new BufferedReader(new InputStreamReader(arquivo, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode json_formatado = mapper.readTree(conteudo);
            return json_formatado;
        } catch (Exception e){
            System.out.println("Erro ao transformar JSON: " + e.getMessage());
        }
        return null;
    }
}
