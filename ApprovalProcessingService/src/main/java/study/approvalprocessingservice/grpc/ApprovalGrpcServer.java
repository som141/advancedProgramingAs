package study.approvalprocessingservice.grpc;

import approval.ApprovalGrpc;
import approval.ApprovalOuterClass;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import study.approvalprocessingservice.queue.ApprovalQueue;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ApprovalGrpcServer extends ApprovalGrpc.ApprovalImplBase {

    private final ApprovalQueue approvalQueue;

    @Override
    public void requestApproval(ApprovalOuterClass.ApprovalRequest request,
                                StreamObserver<ApprovalOuterClass.ApprovalResponse> responseObserver) {

        log.info("Received RequestApproval: requestId={}", request.getRequestId());

        ApprovalOuterClass.Step firstPending = request.getStepsList().stream()
                .filter(s -> "pending".equals(s.getStatus()))
                .findFirst()
                .orElse(null);

        if (firstPending != null) {
            int approverId = firstPending.getApproverId();
            approvalQueue.enqueueForApprover(approverId, request);
            log.info("Enqueued request {} for approver {}", request.getRequestId(), approverId);
        } else {
            log.warn("No pending step for request {}", request.getRequestId());
        }

        ApprovalOuterClass.ApprovalResponse response =
                ApprovalOuterClass.ApprovalResponse.newBuilder()
                        .setStatus("received")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
