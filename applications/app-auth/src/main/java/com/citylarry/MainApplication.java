package com.citylarry;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(info = @Info(
        title = "CrediYa - Authentication API",
        version = "1.0.0",
        description = "API for user management and authentication in the CrediYa platform."
))
@SpringBootApplication(scanBasePackages = "com.citylarry")
public class MainApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(MainApplication.class, args);
    }
}
