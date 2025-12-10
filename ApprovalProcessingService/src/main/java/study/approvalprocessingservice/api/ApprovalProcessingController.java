package study.approvalprocessingservice.api;

import approval.ApprovalOuterClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.approvalprocessingservice.api.dto.ApprovalQueueItemDto;
import study.approvalprocessingservice.queue.ApprovalQueue;
import study.approvalprocessingservice.grpc.ApprovalResultGrpcClient;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ApprovalProcessingController {

    private final ApprovalQueue approvalQueue;
    private final ApprovalResultGrpcClient resultClient;

    /**
     * 특정 승인자의 대기 결재 목록 조회
     * GET /process/{approverId}
     */
    @GetMapping("/{approverId}")
    public List<ApprovalQueueItemDto> getQueue(
            @PathVariable int approverId
    ) {
        // Proto(ApprovalRequest) -> DTO 변환 후 반환
        return approvalQueue.getQueue(approverId).stream()
                .map(this::toDto)
                .toList();
    }

    // Proto 메시지를 REST 응답용 DTO로 변환
    private ApprovalQueueItemDto toDto(ApprovalOuterClass.ApprovalRequest req) {
        List<ApprovalQueueItemDto.StepDto> stepDtos = req.getStepsList().stream()
                .map(s -> new ApprovalQueueItemDto.StepDto(
                        s.getStep(),
                        s.getApproverId(),
                        s.getStatus()
                ))
                .toList();

        return new ApprovalQueueItemDto(
                req.getRequestId(),
                req.getRequesterId(),
                req.getTitle(),
                req.getContent(),
                stepDtos
        );
    }

    /**
     * 승인자가 승인/반려 처리
     * POST /process/{approverId}/{requestId}
     *
     * body: { "status": "approved" } 또는 { "status": "rejected" }
     */
    @PostMapping("/{approverId}/{requestId}")
    public ResponseEntity<Void> decide(
            @PathVariable int approverId,
            @PathVariable int requestId,
            @RequestBody DecisionRequest body
    ) {
        Optional<ApprovalOuterClass.ApprovalRequest> popped =
                approvalQueue.pop(approverId, requestId);

        if (popped.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ApprovalOuterClass.ApprovalRequest req = popped.get();

        ApprovalOuterClass.Step step = req.getStepsList().stream()
                .filter(s -> s.getApproverId() == approverId)
                .findFirst()
                .orElseThrow();

        // gRPC로 Request Service에 결과 전송
        resultClient.sendResult(
                req.getRequestId(),
                step.getStep(),
                approverId,
                body.getStatus()
        );

        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    public static class DecisionRequest {
        // "approved" or "rejected"
        private String status;
    }
}
