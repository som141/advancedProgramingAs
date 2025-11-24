package study.employeeservice.domain.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployController {

    private final EmployService employService;

    /**
     * 직원 생성
     * POST /api/employees
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmployDto.Response>> create(
            @RequestBody @Valid EmployDto.requestDto request
    ) {
        EmployDto.Response response = employService.create(request);
        return ApiResponse.success(EmploySuccessCode.EMPLOYEE_CREATE_SUCCESS,response);
    }

    /**
     * 직원 단건 조회
     * GET /api/employees/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployDto.Response>> getById(
            @PathVariable Long id
    ) {
        EmployDto.Response response = employService.getById(id);
        return ApiResponse.success(EmploySuccessCode.EMPLOYEE_READ_SUCCESS,response);
    }

    /**
     * 직원 전체 조회
     * GET /api/employees
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployDto.Response>>> getAll() {
        List<EmployDto.Response> list = employService.getAll();
        return ApiResponse.success(EmploySuccessCode.EMPLOYEE_LIST_SUCCESS,list);
    }

    /**
     * 직원 수정
     * PUT /api/employees/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployDto.Response>> update(
            @PathVariable Long id,
            @RequestBody @Valid EmployDto.requestDto request
    ) {
        EmployDto.Response response = employService.update(id, request);
        return ApiResponse.success(EmploySuccessCode.EMPLOYEE_UPDATE_SUCCESS,response);
    }

    /**
     * 직원 삭제
     * DELETE /api/employees/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        employService.delete(id);
        return ApiResponse.success(EmploySuccessCode.EMPLOYEE_DELETE_SUCCESS);  // data 없는 성공 응답
    }
}
