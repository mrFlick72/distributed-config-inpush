package it.valeriovaudi.lab.distributedconfiginpush.configuration.streaming.waiter;

public class Waiter {

    public void waitFor(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
