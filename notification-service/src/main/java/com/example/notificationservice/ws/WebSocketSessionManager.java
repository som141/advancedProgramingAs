package com.example.notificationservice.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionManager {

    // key: employeeId, value: WebSocketSession
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(Long employeeId, WebSocketSession session) {
        sessions.put(employeeId, session);
        log.info("WebSocket connected: employeeId={}, sessionId={}", employeeId, session.getId());
    }

    public void removeSessionBySession(WebSocketSession session) {
        sessions.entrySet().removeIf(entry -> {
            boolean match = entry.getValue().getId().equals(session.getId());
            if (match) {
                log.info("WebSocket disconnected: employeeId={}, sessionId={}",
                        entry.getKey(), session.getId());
            }
            return match;
        });
    }

    public void sendToEmployee(Long employeeId, String payload) {
        WebSocketSession session = sessions.get(employeeId);
        if (session == null || !session.isOpen()) {
            log.warn("No active WebSocket session for employeeId={}", employeeId);
            return;
        }
        try {
            session.sendMessage(new TextMessage(payload));
            log.info("Sent message to employeeId={}: {}", employeeId, payload);
        } catch (IOException e) {
            log.error("Failed to send message to employeeId={}", employeeId, e);
        }
    }
}
