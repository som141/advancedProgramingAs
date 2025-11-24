package study.employeeservice.domain.employee;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;


public class EmployDto {
    //create,update 시에 씀..
    record requestDto(
    @NotBlank String name,
    @NotBlank String department,
    @NotBlank String position
    ){}

    public record Response(
            Long id,
            String name,
            String department,
            String position,
            LocalDateTime createdAt
    ) {
        public static Response from(Employees e) {
            return new Response(
                    e.getId(),
                    e.getName(),
                    e.getDepartment(),
                    e.getPosition(),
                    e.getCreatedAt()
            );
        }
    }



}