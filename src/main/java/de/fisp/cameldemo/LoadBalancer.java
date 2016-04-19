package de.fisp.cameldemo;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.processor.loadbalancer.LoadBalancerSupport;

public class LoadBalancer extends LoadBalancerSupport {

    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {

            getProcessors().get(0).process(exchange);

        } catch (Throwable e) {
            exchange.setException(e);
        }
        callback.done(true);
        return true;
    }
}