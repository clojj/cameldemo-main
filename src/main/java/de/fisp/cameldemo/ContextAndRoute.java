package de.fisp.cameldemo;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.interceptor.DefaultTraceFormatter;
import org.apache.camel.processor.interceptor.Tracer;

public class ContextAndRoute extends RouteBuilder {

    public static void main(String... args) throws Exception {
        Main main = new Main();

        main.addRouteBuilder(new ContextAndRoute());
        main.run(args);
    }

    public void configure() {

//        configureTracer();

        // TODO create Camel routes here.

/*
        from("file:src/data?noop=true").
            choice().
                when(xpath("/person/city = 'London'")).to("file:target/messages/uk").
                otherwise().to("file:target/messages/others");
*/

        restConfiguration().component("restlet").host("localhost").port(8082).bindingMode(RestBindingMode.auto);

        rest("/users/")
                .id("REST ENDPOINT")
                .post().type(UserPojo.class)
                .to("direct:send-off");
//                .to("direct:rest-in");

        ProducerTemplate template = getContext().createProducerTemplate();

        from("direct:send-off")
                .to("log:THROUGHPUT?level=INFO&groupSize=20")
                .process(new DemoProcessor(template));

        from("seda:seda-rest-in?concurrentConsumers=8")
                .id("SEDA-REST-IN")
                .bean(new SomeBean())
                .to("log:THROUGHPUT-AFTER-SEDA?level=INFO&groupSize=10")
                .to("direct:newUser");

        from("direct:rest-in")
                .id("REST-IN")
                .bean(new SomeBean())
                .to("direct:newUser");

        from("direct:newUser")
                .id("NEWUSERBEAN")
                .bean(new NewUserBean())
                .to("log:THROUGHPUT-NEWUSERBEAN?level=INFO&groupSize=10");

/*
        from("direct:traced")
                .process(new MyTraceMessageProcessor());
*/
//                .to("direct:rx");

/*
        ReactiveCamel rx = new ReactiveCamel(getContext());
        Observable<Message> observable = rx.toObservable("direct:rx");
        Subscription subscription = observable.filter(message -> message.getBody().toString().contains("direct")).subscribe(message -> System.out.println("RX " + message.getBody()));
*/
    }

    private void configureTracer() {
/*
        BacklogTracer tracer = BacklogTracer.createTracer(getContext());
        tracer.setEnabled(true);
        getContext().setDefaultBacklogTracer(tracer);
*/
        getContext().setTracing(true);

        Tracer tracer = new Tracer();
        DefaultTraceFormatter defaultTraceFormatter = tracer.getDefaultTraceFormatter();
        defaultTraceFormatter.setShowExchangeId(true);
//        defaultTraceFormatter.setShowBody(false);
//        defaultTraceFormatter.setShowBodyType(true);
//        defaultTraceFormatter.setShowBreadCrumb(true);
//        defaultTraceFormatter.setShowNode(true);
//        defaultTraceFormatter.setShowOutBodyType(true);
//        defaultTraceFormatter.setShowOutHeaders(true);

        tracer.setTraceOutExchanges(true);
        tracer.setEnabled(true);
//        tracer.setDestinationUri("direct:traced");

        getContext().addInterceptStrategy(tracer);
    }

}
