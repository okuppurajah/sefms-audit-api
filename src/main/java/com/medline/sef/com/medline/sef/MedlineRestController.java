package com.medline.sef.com.medline.sef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Sinks;

@RestController
@Profile("manual")
public class MedlineRestController {


    private static final Logger LOGGER = LoggerFactory.getLogger(MedlineRestController.class);

    @Autowired
    private Sinks.Many<Message<String>> many;

    @PostMapping("/messages/reactive")
    public ResponseEntity<String> reactiveSendMessage(@RequestParam String message) {
        LOGGER.info("Reactive method to send message: {} to destination.", message);
        many.emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }
}
