package com.community.demo.security;

import com.community.demo.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CustomAuthUser implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // 일반 폼 로그인 시 사용하는 생성자
    public CustomAuthUser(User user) {
        this.user = user;
    }

    // 소셜 로그인 시 사용하는 생성자 (사용자 정보 attributes 포함)
    public CustomAuthUser(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }


    /**
     * 타임리프에서 principal.nickName 으로 접근할 수 있게 해주는 브릿지 메서드
     */
    public String getNickName() {
        return user.getNickName();
    }


    /**
     * OAuth2User 인터페이스 메서드: 소셜 서비스에서 받은 속성 정보 반환
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * UserDetails & OAuth2User 공통: 사용자의 권한 목록을 반환
     * User 엔티티의 authList(List<AuthUser>)를 스트림으로 변환하여 ROLE_ 접두사를 붙여 매핑
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthList().stream()
                .map(authUser -> new SimpleGrantedAuthority("ROLE_" + authUser.getAuth().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPwd();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * OAuth2User 인터페이스 메서드: 사용자의 식별자 또는 이름을 반환
     * 우리 프로젝트에서는 이메일 또는 닉네임을 반환하도록 설정
     */
    @Override
    public String getName() {
        return user.getNickName() != null ? user.getNickName() : user.getEmail();
    }

    // 계정 만료 여부 (true: 만료 안됨)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부 (true: 잠기지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명(비밀번호) 만료 여부 (true: 만료 안됨)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부 (true: 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }
}