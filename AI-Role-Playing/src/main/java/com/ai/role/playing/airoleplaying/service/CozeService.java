package com.ai.role.playing.airoleplaying.service;


import com.ai.role.playing.airoleplaying.config.CozeConfig;
import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.model.ChatEventType;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.config.Consts;
import com.coze.openapi.service.service.CozeAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CozeService {

    private static final Logger logger = LoggerFactory.getLogger(CozeService.class);

    private final CozeConfig cozeConfig;
    private final CozeAPI coze;

    @Autowired
    public CozeService(CozeConfig cozeConfig) {
        this.cozeConfig = cozeConfig;
        TokenAuth authCli = new TokenAuth(cozeConfig.getApiKey());
        this.coze = new CozeAPI.Builder()
                .baseURL(Consts.COZE_CN_BASE_URL)
                .auth(authCli)
                .readTimeout(10000)
                .build();
    }

    public String chat(String message) {
        try {
            CreateChatReq req = CreateChatReq.builder()
                    .botID(cozeConfig.getBotId())
                    .userID("default_user")
                    .messages(Collections.singletonList(Message.buildUserQuestionText(message)))
                    .build();

            logger.info("发送请求到 Coze API: {}", req);

            StringBuilder responseBuilder = new StringBuilder();
            coze.chat().stream(req).blockingForEach(event -> {
                if (ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent())) {
                    responseBuilder.append(event.getMessage().getContent());
                }
                if (ChatEventType.CONVERSATION_CHAT_COMPLETED.equals(event.getEvent())) {
                    logger.info("Token usage: {}", event.getChat().getUsage().getTokenCount());
                }
            });

            String response = responseBuilder.toString();
            logger.info("收到响应: {}", response);

            return response.isEmpty() ? "无法获取响应" : response;
        } catch (Exception e) {
            logger.error("调用 Coze API 时发生错误", e);
            return "调用 Coze API 时发生错误: " + e.getMessage();
        }
    }
}
