package com.community.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude="authList")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User extends TimeBase{
    @Id
    private String email;
    @Column(nullable = false)
    private String pwd;
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;


    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthUser> authList = new ArrayList<>();

    public void addAuth(AuthRole role){
        if(this.authList == null){
            this.authList = new ArrayList<>();

        }
        this.authList.add(AuthUser.builder()
                .user(this)
                .auth(role)
                .build());
    }

    public User update(String nickName) {
        this.nickName = nickName;
        return this;
    }

    // 소셜 가입 시 기본 권한 체크 및 추가
    public void setupDefaultRole() {
        if (this.authList == null || this.authList.isEmpty()) {
            this.addAuth(AuthRole.USER); // AuthRole.USER는 "USER" 또는 "ROLE_USER"를 가진 Enum
        }
    }
}
