package study.employeeservice.domain.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import study.employeeservice.global.BaseErrorCode;
@AllArgsConstructor
@Getter
public enum EmployErrorCode implements BaseErrorCode {
    EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "직원을 찾을 수 없습니다."),
    EMPLOYEE_CREATE_FAILED(HttpStatus.BAD_REQUEST, "E002", "직원 생성에 실패했습니다."),
    EMPLOYEE_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "E003", "직원 수정에 실패했습니다."),
    EMPLOYEE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "E004", "직원 삭제에 실패했습니다."),
    EMPLOYEE_LIST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E005", "직원 목록 조회에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


}
