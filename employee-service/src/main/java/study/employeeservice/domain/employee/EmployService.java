package study.employeeservice.domain.employee;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class EmployService {

    private final EmployeeRepository repository;

    @Transactional
    public EmployDto.Response create(EmployDto.requestDto request){
        Employees employees = Employees.builder().name(request.name())
                .department(request.department())
                .position(request.position())
                .build();
        Employees saved =repository.save(employees);
        return EmployDto.Response.from(saved);
    }

    @Transactional(readOnly = true)
    public EmployDto.Response getById(Long id){
        Employees employees = repository.findById(id).orElseThrow(()->new BusinessException(EmployErrorCode.EMPLOYEE_NOT_FOUND));
        return EmployDto.Response.from(employees);
    }

    @Transactional(readOnly = true)
    public List<EmployDto.Response> getAll(){
        return repository.findAll().stream().map(EmployDto.Response::from).toList();
    }

    @Transactional
    public EmployDto.Response update(Long id, EmployDto.requestDto request){
        Employees employees = repository.findById(id)
                .orElseThrow(() -> new BusinessException(EmployErrorCode.EMPLOYEE_NOT_FOUND));

        employees.setName(request.name());
        employees.setDepartment(request.department());
        employees.setPosition(request.position());

        // 변경 감지(dirty checking)로 UPDATE
        return EmployDto.Response.from(employees);
    }

    @Transactional
    public void delete(Long id){
        if (!repository.existsById(id)) {
            throw new BusinessException(EmployErrorCode.EMPLOYEE_NOT_FOUND);
        }
        repository.deleteById(id);
    }



}
