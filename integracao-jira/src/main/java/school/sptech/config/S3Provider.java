package school.sptech.config;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Provider {

    public static S3Client criarCliente() {
        AwsSessionCredentials credentials =
                AwsSessionCredentials.create(
                        S3Connection.getACCESS_KEY(),
                        S3Connection.getSECRET_ACCESS_KEY(),
                        S3Connection.getSESSION_TOKEN()
                );

        return S3Client.builder()
                .region(
                        Region.of(S3Connection.getREGION())
                )
                .credentialsProvider(
                        StaticCredentialsProvider.create(credentials)
                )
                .build();
    }
}
