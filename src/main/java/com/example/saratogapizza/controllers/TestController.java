package com.example.saratogapizza.controllers;

import com.example.saratogapizza.services.RabbitSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class TestController {

    private final RabbitSender rabbitSender;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World 2");
    }
    @GetMapping("/test2")
    public ResponseEntity<String> test2() {
        return ResponseEntity.ok("Hello World 22");
    }

    @GetMapping("/send")
    public ResponseEntity<String> send(@RequestParam String message) {
        rabbitSender.sendTestMessage(message);
        return ResponseEntity.ok("Message sent -> "+ message);

    }
}
