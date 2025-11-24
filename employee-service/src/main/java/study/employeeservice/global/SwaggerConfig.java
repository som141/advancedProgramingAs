package study.employeeservice.global;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Service API")
                        .description("직원 CRUD API 명세서")
                        .version("1.0.0")
                );
    }
}
