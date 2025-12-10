package study.approvalprocessingservice.queue;

import approval.ApprovalOuterClass;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ApprovalQueue {

    private final Map<Integer, List<ApprovalOuterClass.ApprovalRequest>> queues =
            new ConcurrentHashMap<>();

    public void enqueueForApprover(int approverId, ApprovalOuterClass.ApprovalRequest request) {
        queues.computeIfAbsent(approverId, k -> new ArrayList<>()).add(request);
    }

    public List<ApprovalOuterClass.ApprovalRequest> getQueue(int approverId) {
        return Collections.unmodifiableList(
                queues.getOrDefault(approverId, Collections.emptyList())
        );
    }

    public Optional<ApprovalOuterClass.ApprovalRequest> pop(int approverId, int requestId) {
        List<ApprovalOuterClass.ApprovalRequest> list = queues.get(approverId);
        if (list == null) return Optional.empty();

        Iterator<ApprovalOuterClass.ApprovalRequest> it = list.iterator();
        while (it.hasNext()) {
            ApprovalOuterClass.ApprovalRequest r = it.next();
            if (r.getRequestId() == requestId) {
                it.remove();
                if (list.isEmpty()) {
                    queues.remove(approverId);
                }
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }
}
