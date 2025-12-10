package study.approvalprocessingservice.api.dto;

import java.util.List;

public record ApprovalQueueItemDto(
        int requestId,
        int requesterId,
        String title,
        String content,
        List<StepDto> steps
) {
    public record StepDto(
            int step,
            int approverId,
            String status
    ) {}
}
