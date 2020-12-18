package it.valeriovaudi.lab.distributedconfiginpush.kinesis.events.consumer.creation;

import it.valeriovaudi.lab.distributedconfiginpush.kinesis.events.consumer.waiter.Waiter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.ConsumerStatus;

@Component
public class KinesisConsumerIsStillNotActiveEventListener implements ApplicationListener<KinesisConsumerIsStillNotActiveEvent> {

    private final Waiter waiter = new Waiter();
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KinesisAsyncClient kinesisClient;

    public KinesisConsumerIsStillNotActiveEventListener(ApplicationEventPublisher applicationEventPublisher,
                                                        KinesisAsyncClient kinesisClient) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kinesisClient = kinesisClient;
    }


    @Override
    public void onApplicationEvent(KinesisConsumerIsStillNotActiveEvent event) {
        String consumerArn = event.getMessage();
        System.out.println("wait a bit");
        waiter.waitFor(1000L);

        kinesisClient.describeStreamConsumer(builder -> builder.consumerARN(consumerArn))
                .whenComplete((describeStreamConsumerResponse, throwable) -> {
                    if (describeStreamConsumerResponse.consumerDescription().consumerStatus() != ConsumerStatus.ACTIVE) {
                        applicationEventPublisher.publishEvent(new KinesisConsumerIsStillNotActiveEvent(this, consumerArn));
                    } else {
                        applicationEventPublisher.publishEvent(new KinesisConsumerIsActiveEvent(this, consumerArn));
                    }
                });
    }
}
