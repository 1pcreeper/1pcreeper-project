package project.general_api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.shared_general_auth_library.interceptor.AuthInterceptor;
import project.general_api_gateway.properties.CorsProperties;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final CorsProperties corsProperties;
    private final AuthInterceptor authInterceptor;
    @Autowired
    public WebConfig(
        CorsProperties corsProperties,
        AuthInterceptor authInterceptor
    ){
        this.corsProperties = corsProperties;
        this.authInterceptor = authInterceptor;
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all paths
            .allowedOrigins(corsProperties.getAllowedOriginsArray()) // Specific origins
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH") // Allowed HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true) // Allow sending cookies and authentication headers
            .allowedOriginPatterns("/**")
            .maxAge(3600); // Preflight request cache duration
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }
}