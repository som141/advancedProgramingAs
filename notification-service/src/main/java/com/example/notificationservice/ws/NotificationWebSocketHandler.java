package com.example.notificationservice.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 쿼리 스트링에서 id 파라미터 추출 (예: ws://localhost:8080/ws?id=1)
        String query = session.getUri().getQuery(); // "id=1"
        Long employeeId = parseEmployeeId(query);

        if (employeeId == null) {
            log.warn("No employee id in query. Closing session: {}", session.getId());
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        sessionManager.addSession(employeeId, session);
    }

    private Long parseEmployeeId(String query) {
        if (query == null) return null;
        for (String part : query.split("&")) {
            String[] kv = part.split("=");
            if (kv.length == 2 && kv[0].equals("id")) {
                try {
                    return Long.parseLong(kv[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 클라이언트에서 보내는 메시지는 특별히 처리할 필요 없으면 무시해도 됨
        log.info("Received message from client: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionManager.removeSessionBySession(session);
    }
}
