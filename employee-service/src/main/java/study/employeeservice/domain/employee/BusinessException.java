package study.employeeservice.domain.employee;

import lombok.Getter;
import study.employeeservice.global.BaseErrorCode;
@Getter
public class BusinessException extends RuntimeException{

    private BaseErrorCode errorCode;

    public BusinessException(BaseErrorCode baseErrorCode){
        super();
        this.errorCode=baseErrorCode;
    }
}
