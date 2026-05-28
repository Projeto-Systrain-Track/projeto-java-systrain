import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import io.github.cdimascio.dotenv.Dotenv;

public class ConexaoS3 {
    public static S3Client criarCliente() {
        Dotenv dotenv = Dotenv.load();
        String accessKey = dotenv.get("AWS_ACCESS_KEY_ID");
        String secretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
        String sessionToken = dotenv.get("AWS_SESSION_TOKEN");
        String regiao = dotenv.get("AWS_REGION");

        try{
            S3Client s3 = S3Client.builder()
                    .region(Region.of(regiao))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsSessionCredentials.create(
                                            accessKey,
                                            secretKey,
                                            sessionToken
                                    )
                            )
                    )
                    .build();
            return s3;
        } catch (Exception e) {
            System.out.println("ERRO AO CRIAR CLIENTE S3: " + e.getMessage());
            return null ;

        }
    }
}
