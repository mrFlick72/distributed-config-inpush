package it.valeriovaudi.lab.distributedconfiginpush.kinesis.events.consumer.waiter;

public class Waiter {

    public void waitFor(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
