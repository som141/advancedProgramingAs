package study.employeeservice.global;

import org.springframework.http.HttpStatus;

public interface BaseSuccessCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
}