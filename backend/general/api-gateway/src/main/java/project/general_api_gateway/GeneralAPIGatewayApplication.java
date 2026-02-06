package project.general_api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {
    "project.general_api_gateway",
    "project.shared_general_auth_library",
    "project.shared_starter"
})
public class GeneralAPIGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneralAPIGatewayApplication.class, args);
    }
}
