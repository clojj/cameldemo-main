package de.fisp.cameldemo;

import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import java.util.concurrent.Future;

class AsyncDemoProcessor implements AsyncProcessor {
    private final ProducerTemplate template;

    public AsyncDemoProcessor(ProducerTemplate template) {
        this.template = template;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback asyncCallback) {
        Future<Exchange> future = template.asyncSend("seda:seda-rest-in", exchange);
        asyncCallback.done(true);
        return true;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        throw new RuntimeException("Unexpected use of synchronous API");
    }
}
