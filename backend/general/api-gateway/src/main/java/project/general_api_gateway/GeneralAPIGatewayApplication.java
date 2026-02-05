package project.general_api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;

@SpringBootApplication
public class GeneralAPIGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneralAPIGatewayApplication.class, args);
    }
}
