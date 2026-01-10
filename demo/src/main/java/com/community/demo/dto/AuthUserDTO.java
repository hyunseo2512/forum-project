package com.community.demo.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@Getter
@Setter
@ToString
public class AuthUserDTO extends org.springframework.security.core.userdetails.User {

    private String email;
    private String nickName;
    private String role;
    private long id; // AuthUser 엔티티의 ID가 필요하다면 유지

    // CustomUserDetailsService와 UserService에서 사용할 생성자
    public AuthUserDTO(String username, String password, String nickName, String role, long id,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = username;
        this.nickName = nickName;
        this.role = role;
        this.id = id;
    }
}