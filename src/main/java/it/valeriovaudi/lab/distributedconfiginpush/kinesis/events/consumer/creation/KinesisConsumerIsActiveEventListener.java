package it.valeriovaudi.lab.distributedconfiginpush.kinesis.events.consumer.creation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandlerFromBuilder;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.*;

import java.util.concurrent.CompletableFuture;

@Component
public class KinesisConsumerIsActiveEventListener implements ApplicationListener<KinesisConsumerIsActiveEvent> {

    private final KinesisAsyncClient kinesisClient;
    private final String shardId;

    public KinesisConsumerIsActiveEventListener(@Value("${aws.kinesis.shardId}") String shardId,
                                                KinesisAsyncClient kinesisClient) {
        this.kinesisClient = kinesisClient;
        this.shardId = shardId;
    }

    @Override
    public void onApplicationEvent(KinesisConsumerIsActiveEvent event) {
        String consumerArn = event.getMessage();
        SubscribeToShardRequest request = SubscribeToShardRequest.builder()
                .shardId(shardId)
                .consumerARN(consumerArn)
                .startingPosition(s -> s.type(ShardIteratorType.LATEST))
                .build();

        SubscribeToShardResponseHandler subscriber = SubscribeToShardResponseHandler
                .builder()
                .subscriber(p -> Flux.just(p)
                        .ofType(SubscribeToShardEvent.class)
                        .flatMapIterable(SubscribeToShardEvent::records)
                        .subscribe(e -> System.out.println("Record batch = " + e.data().asUtf8String())))
                .build();

        kinesisClient.subscribeToShard(request, subscriber);
    }
}
