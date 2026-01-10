package com.community.demo.config;

import com.community.demo.security.CustomUserService;
import com.community.demo.security.LoginFailuerHandler;
import com.community.demo.security.LoginSuccessHandler;
import com.community.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. PasswordEncoder (기존 유지)
    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 2. UserDetailsService 빈 (이름을 userDetailsService로 고정)
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserService();
    }

    // ★ 3. 핵심: AuthenticationProvider 추가 (이게 없어서 404/실패가 뜬 것입니다)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/list").hasAnyRole("ADMIN")
                        .requestMatchers("/", "/dist/**", "/js/**", "/image/**", "/upload/**",
                                "/board/detail", "/comment/list/**",
                                "/user/join", "/user/login", "/error/**", "/view"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 시큐리티가 위에서 만든 authenticationProvider를 사용하도록 자동으로 설정됩니다.
                .formLogin(login -> login
                        .usernameParameter("email")
                        .passwordParameter("pwd")
                        .loginPage("/user/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")
                )
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new LoginSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return new LoginFailuerHandler();
    }
}