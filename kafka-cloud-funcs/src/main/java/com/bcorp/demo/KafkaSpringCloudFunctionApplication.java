package com.bcorp.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
@SpringBootApplication
public class KafkaSpringCloudFunctionApplication {

    private static final Logger log = LoggerFactory.getLogger(KafkaSpringCloudFunctionApplication.class);
    private final EmitterProcessor<Message<?>> processor = EmitterProcessor.create();

    @Autowired
    private ObjectMapper jsonMapper;

    public static void main(String[] args) {
        SpringApplication.run(KafkaSpringCloudFunctionApplication.class, args);
    }

    @PostMapping(consumes = "*/*")
    @SuppressWarnings("unchecked")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleRequest(@RequestBody String body) throws Exception {
        Map<String, String> payload = jsonMapper.readValue(body, Map.class);
        String text = payload.getOrDefault("text","hello world");
        var message = MessageBuilder
                .withPayload(text)
                .build();
        processor.onNext(message);
    }

    @Bean
    public Function<String, String> processor(){
        return (s) -> new StringBuilder(s).reverse().toString();
    }

    @Bean
    public Consumer<String> sink(){
        return (s) -> log.info("Got message: {}", s);
    }

    @Bean
    public Supplier<Flux<Message<?>>> supplier() {
        return () -> processor;
    }
}
