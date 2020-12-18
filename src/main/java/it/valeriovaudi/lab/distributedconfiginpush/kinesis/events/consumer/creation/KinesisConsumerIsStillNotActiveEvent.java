package it.valeriovaudi.lab.distributedconfiginpush.kinesis.events.consumer.creation;

import org.springframework.context.ApplicationEvent;

public class KinesisConsumerIsStillNotActiveEvent extends ApplicationEvent {
    private String message;

    public KinesisConsumerIsStillNotActiveEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
