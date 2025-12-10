package study.approvalprocessingservice.grpc;

import approval.ApprovalGrpc;
import approval.ApprovalOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApprovalResultGrpcClient {

    @Value("${grpc.approval-request.host}")
    private String host;

    @Value("${grpc.approval-request.port}")
    private int port;

    private ManagedChannel channel;
    private ApprovalGrpc.ApprovalBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        stub = ApprovalGrpc.newBlockingStub(channel);
        log.info("ApprovalResultGrpcClient connected to {}:{}", host, port);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    public void sendResult(int requestId, int step, int approverId, String status) {
        ApprovalOuterClass.ApprovalResultRequest req =
                ApprovalOuterClass.ApprovalResultRequest.newBuilder()
                        .setRequestId(requestId)
                        .setStep(step)
                        .setApproverId(approverId)
                        .setStatus(status)
                        .build();

        ApprovalOuterClass.ApprovalResultResponse resp =
                stub.returnApprovalResult(req);

        log.info("ReturnApprovalResult => status={}", resp.getStatus());
    }
}
