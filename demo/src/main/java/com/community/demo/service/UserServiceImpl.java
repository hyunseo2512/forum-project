package com.community.demo.service;

import com.community.demo.dto.UserDTO;
import com.community.demo.entity.AuthRole;
import com.community.demo.entity.User;
import com.community.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

//    @Transactional
//    @Override
//    public String register(UserDTO userDTO) {
//        userDTO.setPwd(passwordEncoder.encode(userDTO.getPwd()));
//        User user = convertDtoToEntity(userDTO);
//        user.addAuth(AuthRole.USER);
//
//        return userRepository.save(user).getEmail();
//    }

    @Transactional
    @Override
    public String register(UserDTO userDTO) {
        // 1. 비밀번호 일치 확인 (백엔드 검증)
        if (!userDTO.getPwd().equals(userDTO.getPwdConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 2. fName + lName 합쳐서 nickName 설정
        String fullName = userDTO.getLastName() + userDTO.getFirstName();
        userDTO.setNickName(fullName);

        // 3. 비밀번호 암호화
        userDTO.setPwd(passwordEncoder.encode(userDTO.getPwd()));

        // 4. 엔티티 변환 및 저장
        User user = convertDtoToEntity(userDTO);
        user.addAuth(AuthRole.USER);

        return userRepository.save(user).getEmail();
    }

    @Override
    public List<UserDTO> getList() {
        List<User> userList = userRepository.findAllWithAuthList();

        return userList.stream()
                .map(this::convertEntityToDTO)
                .toList();
    }

    @Override
    public void lastLoginUpdate(String name) {

    }

    @Override
    @Transactional
    public void modify(UserDTO userDTO) {
        // 1. 기존 유저 정보 가져오기 (권한 포함)
        Optional<User> result = userRepository.findById(userDTO.getEmail());
        User user = result.orElseThrow();

        // 2. 닉네임 변경
        user.setNickName(userDTO.getNickName());

        // 3. 비밀번호 변경 (입력값이 있을 경우에만)
        if (userDTO.getPwd() != null && !userDTO.getPwd().isEmpty()) {
            user.setPwd(passwordEncoder.encode(userDTO.getPwd()));
        }

        // 4. 변경 감지(Dirty Checking)에 의해 자동으로 update 쿼리 실행
        userRepository.save(user);
    }
}
