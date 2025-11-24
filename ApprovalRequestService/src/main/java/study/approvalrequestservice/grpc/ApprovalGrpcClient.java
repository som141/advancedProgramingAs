package study.approvalrequestservice.grpc;

import approval.*;
import approval.ApprovalOuterClass;
import study.approvalrequestservice.mongo.ApprovalRequestDocument;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApprovalGrpcClient {

    @Value("${grpc.approval-processing.host}")
    private String host;

    @Value("${grpc.approval-processing.port}")
    private int port;

    private ManagedChannel channel;
    private ApprovalGrpc.ApprovalBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // TLS 없이
                .build();
        stub = ApprovalGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    public void sendRequestApproval(ApprovalRequestDocument doc) {
        // ApprovalRequest 메시지 빌드
        ApprovalOuterClass.ApprovalRequest.Builder builder =
                ApprovalOuterClass.ApprovalRequest.newBuilder()
                        .setRequestId(doc.getRequestId().intValue())
                        .setRequesterId(doc.getRequesterId().intValue())
                        .setTitle(doc.getTitle())
                        .setContent(doc.getContent());

        doc.getSteps().forEach(s ->
                builder.addSteps(
                        ApprovalOuterClass.Step.newBuilder()
                                .setStep(s.getStep())
                                .setApproverId(s.getApproverId().intValue())
                                .setStatus(s.getStatus())
                                .build()
                )
        );

        ApprovalOuterClass.ApprovalRequest request = builder.build();

        // gRPC 호출
        ApprovalOuterClass.ApprovalResponse response =
                stub.requestApproval(request);

        log.info("gRPC RequestApproval response: {}", response.getStatus());
    }
}
