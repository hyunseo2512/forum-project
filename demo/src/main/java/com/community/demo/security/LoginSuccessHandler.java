package com.community.demo.security;

import com.community.demo.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. 유저 식별자(이메일) 추출
        String email = extractEmail(authentication);

        // 2. 마지막 로그인 시간 업데이트
        if (email != null) {
            userService.lastLoginUpdate(email);
        }

        // 3. 세션 에러 메시지 제거
        HttpSession ses = request.getSession(false);
        if (ses != null) {
            ses.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

        // 4. 리다이렉트 처리 (로그인 전 페이지 또는 메인)
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String targetUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : "/";

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    // 로그인 방식에 따라 이메일을 추출하는 메서드
    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        // 일반 로그인 (UserDetails)
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }

        // 소셜 로그인 (OAuth2User)
        else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            var oAuth2User = (org.springframework.security.oauth2.core.user.OAuth2User) principal;
            var attributes = oAuth2User.getAttributes();

            // Google
            if (attributes.containsKey("email")) {
                return (String) attributes.get("email");
            }
            // Naver
            if (attributes.containsKey("response")) {
                var response = (java.util.Map<String, Object>) attributes.get("response");
                return (String) response.get("email");
            }
            // Kakao
            if (attributes.containsKey("kakao_account")) {
                var kakaoAccount = (java.util.Map<String, Object>) attributes.get("kakao_account");
                return (String) kakaoAccount.get("email");
            }
        }

        return authentication.getName();
    }
}
