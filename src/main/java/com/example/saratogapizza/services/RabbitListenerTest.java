package com.example.saratogapizza.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import static com.example.saratogapizza.configs.RabbitConfig.*;

@Component
public class RabbitListenerTest {

    @RabbitListener(queues = QUEUE)
    public void receiveMessage(String message) {
        System.out.println("***************************************************************");
        System.out.println("📩 Received message: " + message);
        System.out.println("***************************************************************");
    }
}
