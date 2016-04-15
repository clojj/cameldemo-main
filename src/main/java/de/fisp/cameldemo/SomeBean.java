package de.fisp.cameldemo;

public class SomeBean {
    public String process(String body) throws InterruptedException {
        Thread.sleep(500);
        return body;
    }
}
