package project.general_api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import project.general_api_gateway.properties.CorsProperties;
import project.general_api_gateway.properties.JwtProperties;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final CorsProperties corsProperties;
    private final JwtProperties jwtProperties;
    @Autowired
    public SecurityConfig(
        CorsProperties corsProperties,
        JwtProperties jwtProperties
    ){
        this.corsProperties = corsProperties;
        this.jwtProperties = jwtProperties;
    }
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return ReactiveJwtDecoders.fromIssuerLocation(
            jwtProperties.getIssuerUri()
        );
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // Define access policies
            .authorizeExchange(exchanges -> exchanges
                // Allow public access to certain paths, e.g., login or health checks
                .pathMatchers("/api/public/**").permitAll()
                // Secure all other routes
                .anyExchange().authenticated()
            )
            // Enable OAuth2 resource server (for Bearer token validation)
            .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder())) // Use JWT validation based on properties
            )
            .csrf(csrf->
                csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()
                )
        );

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            corsProperties.getAllowedOriginsArray()
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
