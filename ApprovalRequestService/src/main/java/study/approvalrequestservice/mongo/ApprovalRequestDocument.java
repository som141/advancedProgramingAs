package study.approvalrequestservice.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "approval_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestDocument {

    @Id
    private String id;          // MongoDB _id (ObjectId → String)

    private Long requestId;     // 우리가 관리하는 결재 요청 ID (1, 2, 3, ...)
    private Long requesterId;
    private String title;
    private String content;

    private List<Step> steps;

    private String finalStatus; // in_progress, approved, rejected
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Step {
        private Integer step;      // 1, 2, 3, ...
        private Long approverId;
        private String status;     // pending, approved, rejected
        private Instant updatedAt; // 이 단계가 처리된 시간
    }
}
