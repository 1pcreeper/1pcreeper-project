package project.general_api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.general_api_gateway.properties.GeneralAccountServiceSpecProperties;

@Configuration
@Slf4j
public class GatewayConfig {

    @Autowired
    private GeneralAccountServiceSpecProperties generalAccountServiceSpecProperties;
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route for account service
            .route("account", r -> r.path("/api/account/**")
                .filters(f -> f
                    .filter(logPath())  
                    .rewritePath("/api/account/(?<segment>.*)", "/${segment}")) 
                .uri("lb://" + generalAccountServiceSpecProperties.getHostName() + ":" + generalAccountServiceSpecProperties.getHttpPort()))
            .build();
    }

    // Custom filter to log the request path
    private GatewayFilter logPath() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            log.debug(path);
            return chain.filter(exchange); 
        };
    }
}