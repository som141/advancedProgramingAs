package study.employeeservice.domain.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import study.employeeservice.global.BaseSuccessCode;

@AllArgsConstructor
@Getter
public enum EmploySuccessCode implements BaseSuccessCode {
    EMPLOYEE_CREATE_SUCCESS(HttpStatus.CREATED, "S001", "직원 생성에 성공했습니다."),
    EMPLOYEE_READ_SUCCESS(HttpStatus.OK, "S002", "직원 조회에 성공했습니다."),
    EMPLOYEE_LIST_SUCCESS(HttpStatus.OK, "S003", "직원 목록 조회에 성공했습니다."),
    EMPLOYEE_UPDATE_SUCCESS(HttpStatus.OK, "S004", "직원 수정에 성공했습니다."),
    EMPLOYEE_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "S005", "직원 삭제에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
