package study.employeeservice.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class health {
    @GetMapping("/health")
    public ResponseEntity<String> health(){
        log.info("helth check");
        return ResponseEntity.ok("health is ok..!");
    }
}
