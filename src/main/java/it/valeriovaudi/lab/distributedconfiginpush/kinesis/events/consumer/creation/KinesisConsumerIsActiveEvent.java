package it.valeriovaudi.lab.distributedconfiginpush.kinesis.events.consumer.creation;

import org.springframework.context.ApplicationEvent;

public class KinesisConsumerIsActiveEvent extends ApplicationEvent {
    private String message;

    public KinesisConsumerIsActiveEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
