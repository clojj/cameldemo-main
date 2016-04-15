/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fisp.cameldemo;

import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.ThroughputLogger;
import org.apache.camel.processor.interceptor.DefaultTraceFormatter;
import org.apache.camel.processor.interceptor.Tracer;
import org.apache.camel.util.CamelLogger;

/**
 * A Camel Router
 *
 * @version $
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new Main();

        main.addRouteBuilder(new MyRouteBuilder());
        main.run(args);
    }

    public void configure() {

//        configureTracer();

        // TODO create Camel routes here.

        // here is a sample which processes the input files
        // (leaving them in place - see the 'noop' flag)
        // then performs content based routing on the message
        // using XPath
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
                .process(new AsyncProcessor() {
                    @Override
                    public boolean process(Exchange exchange, AsyncCallback asyncCallback) {
                        template.asyncSend("seda:seda-rest-in", exchange);
                        return true;
                    }

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw new RuntimeException("Unexpected use of synchronous API");
                    }
                });

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
