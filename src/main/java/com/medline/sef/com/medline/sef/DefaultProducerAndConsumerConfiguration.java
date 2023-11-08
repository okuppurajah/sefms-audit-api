// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.medline.sef.com.medline.sef;


import com.azure.spring.messaging.checkpoint.Checkpointer;
import com.azure.spring.messaging.eventhubs.support.EventHubsHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.azure.spring.messaging.AzureHeaders.CHECKPOINTER;


@Configuration
@Profile("default")
public class DefaultProducerAndConsumerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubBinderApplication.class);

    private int i = 0;

    @Bean
    public Consumer<Message<String>> consume() {
        return message -> {
            Checkpointer checkpointer = (Checkpointer) message.getHeaders().get(CHECKPOINTER);
            LOGGER.info("New message received: '{}', partition key: {}, sequence number: {}, offset: {}, enqueued time: {}",
                    message.getPayload(),
                    message.getHeaders().get(EventHubsHeaders.PARTITION_KEY),
                    message.getHeaders().get(EventHubsHeaders.SEQUENCE_NUMBER),
                    message.getHeaders().get(EventHubsHeaders.OFFSET),
                    message.getHeaders().get(EventHubsHeaders.ENQUEUED_TIME)
            );

            checkpointer.success()
                    .doOnSuccess(success -> LOGGER.info("Message '{}' successfully checkpointed", message.getPayload()))
                    .doOnError(error -> LOGGER.error("Exception found", error))
                    .block();
        };
    }

    @Bean
    public Supplier<Message<String>> supply() {
        return () -> {
            LOGGER.info("Sending message, sequence " + i);
            return MessageBuilder.withPayload(generatePayload() + i++).build();
        };
    }


    public static String generatePayload() {
        return "{\n" +
                "  \"id\": \"1807\",\n" +
                "  \"topic\": \"audit\",\n" +
                "  \"eventType\": \"ecom-audit\",\n" +
                "  \"subject\": \"myapp/vehicles/motorcycles\",\n" +
                "  \"eventTime\": \"2017-08-10T21:03:07+00:00\",\n" +
                "  \"data\": {\n" +
                "    \"make\": \"Ducati\",\n" +
                "    \"applicationID\": \"ecom\",\n" +
                "    \"model\": \"Monster\"\n" +
                "  },\n" +
                "  \"dataVersion\": \"1.0\"\n" +
                "}";
    }


}
