package project.shared_general_starter.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermitAllInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            response.sendError(403, "Invalid Verify Token");
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (
            handlerMethod.hasMethodAnnotation(javax.annotation.security.PermitAll.class) ||
                handlerMethod.hasMethodAnnotation(jakarta.annotation.security.PermitAll.class)
        ) {
            return true; // Allow access
        }
        return false;
    }
}
