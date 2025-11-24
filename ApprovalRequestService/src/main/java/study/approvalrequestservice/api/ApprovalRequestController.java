package study.approvalrequestservice.api;

import study.approvalrequestservice.mongo.ApprovalRequestDocument;
import study.approvalrequestservice.service.ApprovalRequestAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approvals")
public class ApprovalRequestController {

    private final ApprovalRequestAppService appService;

    @PostMapping
    public ResponseEntity<CreateApprovalResponse> createApproval(
            @Valid @RequestBody CreateApprovalRequest req
    ) {
        Long requestId = appService.createApproval(req);
        return ResponseEntity.ok(new CreateApprovalResponse(requestId));
    }

    @GetMapping
    public ResponseEntity<List<ApprovalRequestDocument>> listApprovals() {
        return ResponseEntity.ok(appService.findAll());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ApprovalRequestDocument> getApproval(
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(appService.findByRequestId(requestId));
    }
}
