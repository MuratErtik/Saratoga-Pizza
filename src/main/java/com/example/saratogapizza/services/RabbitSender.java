package com.example.saratogapizza.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import static com.example.saratogapizza.configs.RabbitConfig.*;

@Service
@RequiredArgsConstructor
public class RabbitSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendTestMessage(String message) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
        System.out.println("***************************************************************");
        System.out.println("✅ Sent message: " + message);
        System.out.println("***************************************************************");
    }
}
