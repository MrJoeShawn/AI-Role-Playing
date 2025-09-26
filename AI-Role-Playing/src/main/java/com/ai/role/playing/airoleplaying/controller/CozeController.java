package com.ai.role.playing.airoleplaying.controller;

import com.ai.role.playing.airoleplaying.service.CozeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CozeController {

    @Autowired
    private CozeService cozeService;

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return "消息不能为空";
        }
        return cozeService.chat(message);
    }
}
