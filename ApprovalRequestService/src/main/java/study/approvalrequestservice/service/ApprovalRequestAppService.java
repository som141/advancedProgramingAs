package study.approvalrequestservice.service;

import study.approvalrequestservice.api.CreateApprovalRequest;
import study.approvalrequestservice.grpc.ApprovalGrpcClient;
import study.approvalrequestservice.mongo.ApprovalRequestDocument;
import study.approvalrequestservice.mongo.ApprovalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalRequestAppService {

    private final ApprovalRequestRepository repository;
    private final EmployeeClient employeeClient;
    private final ApprovalGrpcClient grpcClient;

    public Long createApproval(CreateApprovalRequest req) {

        // 1. requester 존재 여부 확인
        if (!employeeClient.existsEmployee(req.requesterId())) {
            throw new IllegalArgumentException("Invalid requesterId: " + req.requesterId());
        }

        // 2. 각 approver 존재 여부 확인
        for (CreateApprovalRequest.StepDto s : req.steps()) {
            if (!employeeClient.existsEmployee(s.approverId())) {
                throw new IllegalArgumentException("Invalid approverId: " + s.approverId());
            }
        }

        // 3. steps가 1부터 연속인지 검증 (1,2,3,...)
        validateSteps(req.steps());

        // 4. Mongo 문서 생성 + 저장
        Long nextId = generateNextRequestId();
        Instant now = Instant.now();

        List<ApprovalRequestDocument.Step> stepDocs = req.steps().stream()
                .sorted(Comparator.comparing(CreateApprovalRequest.StepDto::step))
                .map(s -> ApprovalRequestDocument.Step.builder()
                        .step(s.step())
                        .approverId(s.approverId())
                        .status("pending")
                        .updatedAt(null)
                        .build())
                .toList();

        ApprovalRequestDocument doc = ApprovalRequestDocument.builder()
                .requestId(nextId)
                .requesterId(req.requesterId())
                .title(req.title())
                .content(req.content())
                .steps(stepDocs)
                .finalStatus("in_progress")
                .createdAt(now)
                .updatedAt(now)
                .build();

        repository.save(doc);

        // 5. gRPC로 Approval Processing Service에 전달
        //  -> 서버 아직 없으면 이 줄을 잠깐 주석 처리해도 됨.
        grpcClient.sendRequestApproval(doc);

        return nextId;
    }

    private void validateSteps(List<CreateApprovalRequest.StepDto> steps) {
        List<Integer> sorted = steps.stream()
                .map(CreateApprovalRequest.StepDto::step)
                .sorted()
                .toList();

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i) != i + 1) {
                throw new IllegalArgumentException(
                        "steps must start from 1 and be continuous (1, 2, 3, ...)");
            }
        }
    }

    private Long generateNextRequestId() {
        var last = repository.findFirstByOrderByRequestIdDesc();
        if (last == null || last.getRequestId() == null) {
            return 1L;
        }
        return last.getRequestId() + 1;
    }

    public List<ApprovalRequestDocument> findAll() {
        return repository.findAll();
    }

    public ApprovalRequestDocument findByRequestId(Long requestId) {
        return repository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found: " + requestId));
    }
}
