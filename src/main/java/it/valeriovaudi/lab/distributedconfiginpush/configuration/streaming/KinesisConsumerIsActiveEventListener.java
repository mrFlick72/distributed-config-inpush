package it.valeriovaudi.lab.distributedconfiginpush.configuration.streaming;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.valeriovaudi.lab.distributedconfiginpush.configuration.repository.ApplicationConfigurationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class KinesisConsumerIsActiveEventListener implements ApplicationListener<KinesisConsumerIsActiveEvent> {

    private final ObjectMapper objectMapper;
    private final ApplicationConfigurationRepository repository;
    private final KinesisAsyncClient kinesisClient;
    private final String shardId;

    public KinesisConsumerIsActiveEventListener(ObjectMapper objectMapper, ApplicationConfigurationRepository repository,
                                                @Value("${aws.kinesis.shardId}") String shardId,
                                                KinesisAsyncClient kinesisClient) {
        this.objectMapper = objectMapper;
        this.repository = repository;
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
                .onComplete(() -> System.out.println("stream completed"))
                .onError((ex) -> System.out.println("stream in error: " + ex.getMessage()))
                .subscriber(p -> Flux.just(p)
                        .ofType(SubscribeToShardEvent.class)
                        .flatMapIterable(SubscribeToShardEvent::records)
                        .subscribe(e -> {
                            Map<String, String> content = readJsonFrom(e);
                            repository.storeDataFor(content.get("id"), content.get("content"))
                                    .doOnTerminate(() -> System.out.println("doOnTerminate"))
                                    .doOnCancel(() -> System.out.println("doOnCancel"))
                                    .subscribe();
                        }))
                .build();

        kinesisClient.subscribeToShard(request, subscriber);
    }

    public Map<String, String> readJsonFrom(Record e) {
        Map<String, String> result = new HashMap<>();
        try {
            System.out.println(e.data().asUtf8String());
            JsonNode jsonNode = objectMapper.readTree(new StringReader(e.data().asUtf8String()));
            result = Map.of("id", jsonNode.get("id").asText(), "content", jsonNode.get("content").asText());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return result;
    }

}
