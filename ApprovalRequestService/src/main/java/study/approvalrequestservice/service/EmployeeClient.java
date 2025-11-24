package study.approvalrequestservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class EmployeeClient {

    private final RestClient restClient = RestClient.create();

    @Value("${employee-service.base-url}")
    private String baseUrl;

    public boolean existsEmployee(Long id) {
        String url = baseUrl + "/employees/" + id;
        try {
            restClient.get()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false; // 없는 직원
            }
            throw e; // 다른 에러는 그대로 던짐
        }
    }
}
