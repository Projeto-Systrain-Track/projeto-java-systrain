package school.sptech.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.sptech.config.S3Connection;
import school.sptech.config.S3Provider;
import school.sptech.dto.tela.IncidenteTelaDTO;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

public class S3Service {
    private static final S3Client client = S3Provider.criarCliente();
    private static final String bucket = S3Connection.getBUCKET_NAME();

    public  static List<String> listarEmpresasClient(){
        List<String> empresasDescobertas = new ArrayList<>();

        try{
            ListObjectsV2Request busca = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix("client/")
                    .delimiter("/")
                    .build();

            ListObjectsV2Response resposta = client.listObjectsV2(busca);


            if (resposta.hasCommonPrefixes()) {
                for (CommonPrefix prefixo : resposta.commonPrefixes()) {
                    String nomePasta = prefixo.prefix().replace("client/", "").replace("/", "");

                    if (!nomePasta.isEmpty()) {
                        empresasDescobertas.add(nomePasta);
                    }
                }
            }
            return empresasDescobertas;

        }catch (S3Exception e) {
            System.out.println("[S3Service] Erro da AWS ao tentar listar pastas: " + e.getMessage());
            return empresasDescobertas;
        }catch (Exception e){
            System.out.println("[S3Service] Erro inesperado ao mapear pastas do S3: " + e.getMessage());
            return empresasDescobertas;
        }
    }

    public static List<IncidenteTelaDTO> buscarIncidentesDoS3(String chaveS3) {
        List<IncidenteTelaDTO> listaMapeada = new ArrayList<>();

        try {
            //requisição para buscar o arquivo no S3
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(chaveS3)
                    .build();


            //download do arquivo como um array de bytes e transforma em String
            ResponseBytes<GetObjectResponse> objeto = client.getObjectAsBytes(request);
            String jsonString = objeto.asUtf8String();

            //usa o Jackson ObjectMapper para ler a estrutura do texto JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode raiz = mapper.readTree(jsonString);

            JsonNode arrayIncidentes = raiz.get("lista_incidentes");

            if (arrayIncidentes != null && arrayIncidentes.isArray()) {
                for (JsonNode node : arrayIncidentes) {

                    //jackson pega o nó do JSON e converte direto na estrutura da classe Java DTO
                    IncidenteTelaDTO incidente = mapper.treeToValue(node, IncidenteTelaDTO.class);

                    listaMapeada.add(incidente);
                }
            }

            System.out.println("[S3Service] Arquivo da ETL lido e mapeado com sucesso para Java.");
            return listaMapeada;

        } catch (NoSuchKeyException e) {
            System.out.println("[S3Service] Arquivo " + chaveS3 + " ainda não foi gerado pela ETL no S3.");
            return listaMapeada; //retorna a lista vazia sem quebrar o tudo
        } catch (S3Exception e) {
            System.out.println("[S3Service] Erro de comunicação com a AWS S3: " + e.getMessage());
            return listaMapeada;
        } catch (Exception e) {
            System.out.println("[S3Service] Erro ao traduzir o JSON para DTO Java: " + e.getMessage());
            e.printStackTrace();
            return listaMapeada;
        }
    }
}