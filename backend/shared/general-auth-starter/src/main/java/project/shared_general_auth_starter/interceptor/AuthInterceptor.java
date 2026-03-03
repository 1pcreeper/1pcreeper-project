package project.shared_general_auth_starter.interceptor;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import project.shared_general_auth_starter.constant.CookieKeyConstant;
import project.shared_general_auth_starter.constant.FirebaseClaimKeysConstant;
import project.shared_general_auth_starter.service.auth.FirebaseAuthService;
import project.shared_general_starter.model.entity.enums.AppUserRole;

import java.util.*;
import java.util.stream.Collectors;

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
            UserRecord userRecord = firebaseAuthService.getUser(decodedToken.getUid());
            Authentication authentication = createAuthentication(userRecord);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (FirebaseAuthException e) {
            response.sendError(403, "Invalid Verify Token");
            return false;
        }
    }

    private Authentication createAuthentication(UserRecord userRecord) {
        Set<AppUserRole> roles = findRoles(userRecord);
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return roles.stream().map(
                    r ->
                        new GrantedAuthority() {
                            @Override
                            public String getAuthority() {
                                return r.name();
                            }
                        }
                ).collect(Collectors.toSet());
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return userRecord.getUid();
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return userRecord.getUid();
            }
        };
    }

    private Set<AppUserRole> findRoles(UserRecord userRecord) {
        try {
            Object roleListObject = userRecord.getCustomClaims().getOrDefault(
                FirebaseClaimKeysConstant.ROLE_KEY,
                List.of()
            );
            List<String> roleList = (List<String>) roleListObject;
            return roleList.stream().map(r -> AppUserRole.valueOf(r)).collect(Collectors.toSet());
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

}
