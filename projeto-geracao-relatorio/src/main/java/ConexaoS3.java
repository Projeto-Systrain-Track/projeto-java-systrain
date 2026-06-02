import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

public class ConexaoS3 {
    public static S3Client criarCliente() {
        String regiao = System.getenv("AWS_REGION");
        try{
            S3Client s3 = S3Client.builder()
                    .region(Region.of(regiao))
                    .build();
            return s3;
        } catch (Exception e) {
            System.out.println("ERRO AO CRIAR CLIENTE S3: " + e.getMessage());
            return null ;
        }
    }
}
