package study.approvalrequestservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import study.approvalrequestservice.mongo.ApprovalRequestDocument;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationClient {

    @Value("${notification-service.base-url}")
    private String baseUrl;

    private final RestClient restClient;

    /**
     * 최종 승인 알림
     */
    public void sendFinalApproved(ApprovalRequestDocument doc) {
        String body = """
            {
              "requesterId": %d,
              "requestId": %d,
              "result": "approved",
              "finalResult": "approved"
            }
            """.formatted(doc.getRequesterId(), doc.getRequestId());

        log.info("Send final approved notification: {}", body);

        restClient.post()
                .uri(baseUrl + "/notify/final")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * 최종 반려 알림
     */
    public void sendFinalRejected(ApprovalRequestDocument doc, Long rejectedBy) {
        String body = """
            {
              "requesterId": %d,
              "requestId": %d,
              "result": "rejected",
              "rejectedBy": %d,
              "finalResult": "rejected"
            }
            """.formatted(doc.getRequesterId(), doc.getRequestId(), rejectedBy);

        log.info("Send final rejected notification: {}", body);

        restClient.post()
                .uri(baseUrl + "/notify/final")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
