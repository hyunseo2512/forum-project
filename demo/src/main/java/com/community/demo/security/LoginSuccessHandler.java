package com.community.demo.security;

import com.community.demo.service.UserService;
import groovy.lang.Lazy;
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
    @Lazy // ★ 반드시 추가: Security와 Service 간의 순환 참조를 방지합니다.
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 디버깅용 로그: 핸들러 진입 확인
        System.out.println(">>> LoginSuccessHandler 진입 완료: " + authentication.getName());

        try {
            // 1. 유저 식별자 추출
            String email = extractEmail(authentication);

            // 2. 마지막 로그인 시간 업데이트 (userService가 null인지 반드시 체크)
            if (email != null && userService != null) {
                userService.lastLoginUpdate(email);
            }

            // 3. 세션 에러 메시지 제거
            HttpSession ses = request.getSession(false);
            if (ses != null) {
                ses.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }

            // 4. 리다이렉트 처리 (이미지/404 버그 방지)
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            String targetUrl = "/"; // 기본은 메인

            if (savedRequest != null) {
                String redirectUrl = savedRequest.getRedirectUrl();
                // 이미지 주소나 정적 리소스면 메인으로 강제 이동
                if (redirectUrl.contains("/view") || redirectUrl.contains("/dist") ||
                        redirectUrl.contains("/js") || redirectUrl.contains("/css")) {
                    targetUrl = "/";
                } else {
                    targetUrl = redirectUrl;
                }
                requestCache.removeRequest(request, response);
            }

            System.out.println(">>> 리다이렉트 목적지: " + targetUrl);
            redirectStrategy.sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            // 에러 발생 시 로그 출력 후 메인으로 탈출
            e.printStackTrace();
            redirectStrategy.sendRedirect(request, response, "/");
        }
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
