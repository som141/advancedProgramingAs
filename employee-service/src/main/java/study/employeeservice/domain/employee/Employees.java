package study.employeeservice.domain.employee;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "employees")
public class Employees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(name = "created_at",nullable = false,
            insertable = false,
            updatable = false )
    private LocalDateTime createdAt;


}
