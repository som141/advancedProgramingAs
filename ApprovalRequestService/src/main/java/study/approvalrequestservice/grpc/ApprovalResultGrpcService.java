package study.approvalrequestservice.grpc;

import approval.ApprovalGrpc;
import approval.ApprovalOuterClass;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import study.approvalrequestservice.mongo.ApprovalRequestDocument;
import study.approvalrequestservice.mongo.ApprovalRequestRepository;
import study.approvalrequestservice.service.NotificationClient;

import java.time.Instant;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ApprovalResultGrpcService extends ApprovalGrpc.ApprovalImplBase {

    private final ApprovalRequestRepository repository;
    private final NotificationClient notificationClient;
    private final ApprovalGrpcClient approvalGrpcClient;

    @Override
    public void returnApprovalResult(
            ApprovalOuterClass.ApprovalResultRequest request,
            StreamObserver<ApprovalOuterClass.ApprovalResultResponse> responseObserver
    ) {
        long requestId = request.getRequestId();
        int stepNumber = request.getStep();
        String status = request.getStatus();      // "approved" or "rejected"
        long approverId = request.getApproverId();

        log.info("gRPC returnApprovalResult: requestId={}, step={}, status={}, approverId={}",
                requestId, stepNumber, status, approverId);

        // 1. Load doc from Mongo
        ApprovalRequestDocument doc = repository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("ApprovalRequest not found: " + requestId));

        // 2. Update this step
        doc.getSteps().stream()
                .filter(s -> s.getStep() != null && s.getStep().equals(stepNumber))
                .findFirst()
                .ifPresent(s -> {
                    s.setStatus(status);
                    s.setUpdatedAt(Instant.now());
                });

        // 3. Branch by status
        if ("rejected".equalsIgnoreCase(status)) {
            doc.setFinalStatus("rejected");
            doc.setUpdatedAt(Instant.now());
            repository.save(doc);

            notificationClient.sendFinalRejected(doc, approverId);

        } else if ("approved".equalsIgnoreCase(status)) {
            boolean hasPending = doc.getSteps().stream()
                    .anyMatch(s -> "pending".equalsIgnoreCase(s.getStatus()));

            if (!hasPending) {
                // final approve
                doc.setFinalStatus("approved");
                doc.setUpdatedAt(Instant.now());
                repository.save(doc);

                notificationClient.sendFinalApproved(doc);
            } else {
                // next step exists → send again to Processing
                doc.setUpdatedAt(Instant.now());
                repository.save(doc);

                approvalGrpcClient.sendRequestApproval(doc);
            }
        } else {
            log.warn("Unknown status from ApprovalResultRequest: {}", status);
        }

        // 4. Build response: ApprovalResultResponse
        ApprovalOuterClass.ApprovalResultResponse response =
                ApprovalOuterClass.ApprovalResultResponse.newBuilder()
                        .setStatus("ok")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
