package project.shared_general_auth_library.interceptor;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import project.shared_general_auth_library.constant.CookieKeyConstant;
import project.shared_general_auth_library.service.auth.FirebaseAuthService;

import java.util.Objects;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final FirebaseAuthService firebaseAuthService;
    @Autowired
    public AuthInterceptor(
        FirebaseAuthService firebaseAuthService
    ) {
        this.firebaseAuthService = firebaseAuthService;
    }
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
            return true;
        }
        String idToken = null;
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            response.sendError(403, "Invalid Verify Token");
            return false;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CookieKeyConstant.SECURE)) {
                idToken = cookie.getValue();
            }
        }
        if (Objects.isNull(idToken)) {
            response.sendError(403, "Invalid Verify Token");
            return false;
        }
        try {
            FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);
            return true;
        } catch (FirebaseAuthException  e) {
            response.sendError(403, "Invalid Verify Token");
            return false;
        }
    }
}
