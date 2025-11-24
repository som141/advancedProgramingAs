package study.employeeservice.domain.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import study.employeeservice.global.BaseErrorCode;
import study.employeeservice.global.BaseSuccessCode;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success; // 성공 여부
    private final String code;     // 비즈니스 코드 (예: R001)
    private final String message;  // 기본 메시지
    private final T data;          // 실제 데이터 (ReviewResponse, List<ReviewDto> 등)



    // data 있는 성공
    public static <T> ResponseEntity<ApiResponse<T>> success(BaseSuccessCode code, T data) {
        ApiResponse<T> body = new ApiResponse<>(
                true,
                code.getCode(),
                code.getMessage(),
                data
        );
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }

    // data 없는 성공 (DELETE 등)
    public static ResponseEntity<ApiResponse<Void>> success(BaseSuccessCode code) {
        ApiResponse<Void> body = new ApiResponse<>(
                true,
                code.getCode(),
                code.getMessage(),
                null
        );
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }
    // data 있는 실패
    public static <T> ResponseEntity<ApiResponse<T>> ERROR(BaseErrorCode code, T data) {
        ApiResponse<T> body = new ApiResponse<>(
                false,
                code.getCode(),
                code.getMessage(),
                data
        );

        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }

    // data 없는 실패(DELETE 등)
    public static ResponseEntity<ApiResponse<Void>> ERROR(BaseErrorCode code) {
        ApiResponse<Void> body = new ApiResponse<>(
                false,
                code.getCode(),
                code.getMessage(),
                null
        );
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }
}