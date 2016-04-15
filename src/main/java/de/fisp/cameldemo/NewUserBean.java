package de.fisp.cameldemo;

public class NewUserBean {
    public String process(String userPojo) throws InterruptedException {
        Thread.sleep(1000);
        return "Success";
    }
}
