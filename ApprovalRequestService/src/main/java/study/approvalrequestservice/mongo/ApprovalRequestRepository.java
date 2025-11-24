package study.approvalrequestservice.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApprovalRequestRepository
        extends MongoRepository<ApprovalRequestDocument, String> {

    Optional<ApprovalRequestDocument> findByRequestId(Long requestId);

    // 가장 최근 requestId 찾기용
    ApprovalRequestDocument findFirstByOrderByRequestIdDesc();
}
