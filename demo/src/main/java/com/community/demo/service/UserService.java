package com.community.demo.service;

import com.community.demo.dto.AuthUserDTO;
import com.community.demo.dto.UserDTO;
import com.community.demo.entity.AuthRole;
import com.community.demo.entity.AuthUser;
import com.community.demo.entity.User;

import java.util.List;

public interface UserService {

    // convert DtoToEntity
    default User convertDtoToEntity(UserDTO userDTO){
        return User.builder()
                .email(userDTO.getEmail())
                .pwd(userDTO.getPwd())
                .nickName(userDTO.getNickName())
                .lastLogin((userDTO.getLastLogin()))
                .build();
    }

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

    // authDto 변환
    default AuthUserDTO convertAuthEntityToAuthDto(AuthUser authUser) {
        return AuthUserDTO.builder()
                .id(authUser.getId())
                .role(authUser.getAuth().name())
                .email(authUser.getUser().getEmail())
                .build();
    }

    // authEntity 변환
    default AuthUser convertAuthDTOtoEntity(AuthUserDTO authUserDTO){
        return AuthUser.builder()
                .id(authUserDTO.getId())
                .auth(AuthRole.USER)
                .build();
    }


    void lastLoginUpdate(String name);

    String register(UserDTO userDTO);

    List<UserDTO> getList();

    void modify(UserDTO userDTO);
}
