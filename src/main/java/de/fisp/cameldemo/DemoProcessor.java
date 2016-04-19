package de.fisp.cameldemo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import java.util.concurrent.Future;

class DemoProcessor implements Processor {
    private final ProducerTemplate template;

    public DemoProcessor(ProducerTemplate template) {
        this.template = template;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Future<Exchange> future = template.asyncSend("seda:seda-rest-in", exchange);
//        template.send("seda:seda-rest-in", exchange);
    }
}
