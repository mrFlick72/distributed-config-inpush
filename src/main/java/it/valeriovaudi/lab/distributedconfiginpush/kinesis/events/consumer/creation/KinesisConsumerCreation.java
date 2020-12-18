package it.valeriovaudi.lab.distributedconfiginpush.kinesis.events.consumer.creation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.ConsumerStatus;
import software.amazon.awssdk.services.kinesis.model.DeregisterStreamConsumerResponse;

import javax.annotation.PreDestroy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class KinesisConsumerCreation implements ApplicationRunner {
    private final AtomicReference<String> globalConsumerArn = new AtomicReference<>();

    private final ApplicationEventPublisher applicationEventPublisher;
    private final KinesisAsyncClient kinesisClient;
    private final String kinesisShardArn;

    public KinesisConsumerCreation(ApplicationEventPublisher applicationEventPublisher,
                                   KinesisAsyncClient kinesisClient,
                                   @Value("${aws.kinesis.shardArn}") String kinesisShardArn) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kinesisClient = kinesisClient;
        this.kinesisShardArn = kinesisShardArn;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        kinesisClient.registerStreamConsumer(builder -> builder.streamARN(kinesisShardArn)
                .consumerName(UUID.randomUUID().toString())
        ).thenApply(response -> {
            String consumerArn = response.consumer().consumerARN();
            globalConsumerArn.set(consumerArn);
            return consumerArn;
        }).thenCompose(consumerArn -> kinesisClient.describeStreamConsumer(builder -> builder.consumerARN(consumerArn)))
                .whenComplete((describeStreamConsumerResponse, throwable) -> {
                    if (describeStreamConsumerResponse.consumerDescription().consumerStatus() != ConsumerStatus.ACTIVE) {
                        applicationEventPublisher.publishEvent(new KinesisConsumerIsStillNotActiveEvent(this, globalConsumerArn.get()));
                    } else {
                        applicationEventPublisher.publishEvent(new KinesisConsumerIsActiveEvent(this, globalConsumerArn.get()));
                    }
                });
    }

    private CompletableFuture<DeregisterStreamConsumerResponse> getDeregisterStreamConsumerResponse(String consumerArn) {
        return kinesisClient.deregisterStreamConsumer(builder -> builder.consumerARN(consumerArn).streamARN(kinesisShardArn));
    }

    @PreDestroy
    public void onExit() throws ExecutionException, InterruptedException {
        getDeregisterStreamConsumerResponse(globalConsumerArn.get()).get();
    }
}