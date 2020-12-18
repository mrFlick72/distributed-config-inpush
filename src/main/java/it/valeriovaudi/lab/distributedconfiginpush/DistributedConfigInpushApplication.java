package it.valeriovaudi.lab.distributedconfiginpush;

import it.valeriovaudi.lab.distributedconfiginpush.repository.InMemorySampleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class DistributedConfigInpushApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedConfigInpushApplication.class, args);
    }

    @Bean
    public InMemorySampleRepository inMemorySampleRepository() {
        return new InMemorySampleRepository(new ConcurrentHashMap<>());
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(@Value("${aws.access-key}") String accessKey,
                                                         @Value("${aws.secret-key}") String awsSecretKey) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, awsSecretKey));
    }

    @Bean
    public S3Client s3Client(@Value("${aws.region}") String awsRegion,
                             AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    public KinesisAsyncClient kinesisClient(@Value("${aws.region}") String awsRegion,
                                            AwsCredentialsProvider awsCredentialsProvider) {
        return KinesisAsyncClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.of(awsRegion))
                .build();
    }

}