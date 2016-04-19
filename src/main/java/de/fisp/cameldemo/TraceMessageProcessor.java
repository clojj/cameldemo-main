package de.fisp.cameldemo;

import org.apache.camel.Exchange;
import org.apache.camel.processor.interceptor.TraceEventMessage;

public class TraceMessageProcessor implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
//        String body = exchange.getIn().getBody(String.class);
//        System.out.println("body = " + body);
        Object body = exchange.getIn().getBody();
        TraceEventMessage message = (TraceEventMessage) body;
        System.out.println("message.getPreviousNode() = " + message.getPreviousNode());
//        System.out.println("message.getTimestamp() = " + message.getTimestamp());
        exchange.getOut().setBody(message.getToNode());
    }
}
