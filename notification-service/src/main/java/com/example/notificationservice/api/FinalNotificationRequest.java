package com.example.notificationservice.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalNotificationRequest {

    private Long requesterId;   // 누구에게 보낼지 (직원 ID)
    private Long requestId;     // 결재 요청 ID
    private String result;      // "approved" or "rejected"
    private Long rejectedBy;    // optional, 반려일 때만
    private String finalResult; // "approved" or "rejected"
}
