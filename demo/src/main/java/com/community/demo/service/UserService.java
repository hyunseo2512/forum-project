package com.community.demo.service;

import com.community.demo.dto.AuthUserDTO;
import com.community.demo.dto.UserDTO;
import com.community.demo.entity.AuthRole;
import com.community.demo.entity.AuthUser;
import com.community.demo.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public interface UserService {

    // 1. UserDTO -> User 엔티티 변환 (id 필드 없음)
    default User convertDtoToEntity(UserDTO userDTO){
        return User.builder()
                .email(userDTO.getEmail())
                .pwd(userDTO.getPwd())
                .nickName(userDTO.getNickName())
                .lastLogin(userDTO.getLastLogin())
                .build();
    }

    // 2. User 엔티티 -> UserDTO 변환
    default UserDTO convertEntityToDTO(User user){
        return UserDTO.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .lastLogin(user.getLastLogin())
                .regDate(user.getRegDate())
                .modDate(user.getModDate())
                .authList(user.getAuthList() == null ? null :
                        user.getAuthList().stream()
                                .map(this::convertAuthEntityToAuthDto)
                                .toList())
                .build();
    }

    // UserService 인터페이스 내부 수정
    default AuthUserDTO convertAuthEntityToAuthDto(AuthUser authUser) {
        // 권한 리스트 생성
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + authUser.getAuth().name())
        );

        // [수정] Builder 대신 생성자 호출 (필드 순서 주의!)
        return new AuthUserDTO(
                authUser.getUser().getEmail(),    // username
                authUser.getUser().getPwd(),      // password
                authUser.getUser().getNickName(), // nickName
                authUser.getAuth().name(),        // role
                authUser.getId(),                 // id
                authorities                       // authorities
        );
    }

    // 4. AuthUserDTO -> AuthUser(엔티티) 변환
    default AuthUser convertAuthDTOtoEntity(AuthUserDTO authUserDTO){
        // 주의: 여기서 User 객체를 연결해줘야 함
        return AuthUser.builder()
                .id(authUserDTO.getId()) // AuthUser의 PK id
                .auth(AuthRole.valueOf(authUserDTO.getRole())) // USER 등 Enum 변환
                .build();
    }

    void lastLoginUpdate(String name);
    String register(UserDTO userDTO);
    List<User> getList();
    void modify(UserDTO userDTO);
}