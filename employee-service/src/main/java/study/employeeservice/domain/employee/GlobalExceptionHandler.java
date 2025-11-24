package study.employeeservice.domain.employee;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import study.employeeservice.global.BaseErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex){
        BaseErrorCode errorCode = ex.getErrorCode();
        return ApiResponse.ERROR(errorCode);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");
        // 공통 에러 코드 써도 되고, Employees 전용 만들고 싶으면 Enum 추가
        return ApiResponse.ERROR(
                // 임시로 Generic ErrorCode가 없다고 가정하면 → 간단히 문자열만 data로 감싸도 됨
                new BaseErrorCode() {
                    @Override public org.springframework.http.HttpStatus getHttpStatus() {
                        return org.springframework.http.HttpStatus.BAD_REQUEST;
                    }
                    @Override public String getCode() { return "V001"; }
                    @Override public String getMessage() { return "검증 실패"; }
                },
                msg
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        ex.printStackTrace();
        return ApiResponse.ERROR(
                new BaseErrorCode() {
                    @Override public org.springframework.http.HttpStatus getHttpStatus() {
                        return org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                    @Override public String getCode() { return "E000"; }
                    @Override public String getMessage() { return "서버 내부 오류"; }
                },
                ex.getMessage()
        );
    }
}
