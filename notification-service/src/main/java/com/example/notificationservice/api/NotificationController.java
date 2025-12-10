package com.example.notificationservice.api;

import com.example.notificationservice.ws.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Slf4j
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    @PostMapping("/final")
    public ResponseEntity<Void> sendFinalNotification(
            @RequestBody FinalNotificationRequest request
    ) {
        try {
            // WebSocket으로 보낼 JSON payload 구성
            ObjectNode payloadNode = objectMapper.createObjectNode();
            payloadNode.put("requestId", request.getRequestId());
            payloadNode.put("result", request.getResult());
            payloadNode.put("finalResult", request.getFinalResult());
            if (request.getRejectedBy() != null) {
                payloadNode.put("rejectedBy", request.getRejectedBy());
            }

            String json = objectMapper.writeValueAsString(payloadNode);

            // 해당 요청자에게 WebSocket으로 전송
            sessionManager.sendToEmployee(request.getRequesterId(), json);

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Failed to serialize notification payload", e);
            // 일단 500으로 리턴 (과제용이면 이 정도면 충분)
            return ResponseEntity.internalServerError().build();
        }
    }
}
