package com.community.demo.service;

import com.community.demo.dto.AuthUserDTO;
import com.community.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // [수정] 클래스 이름 앞에 패키지 경로를 붙여서 엔티티임을 명시합니다.
        com.community.demo.entity.User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("이메일을 확인해주세요."));

        // 권한 리스트 변환
        List<SimpleGrantedAuthority> authorities = user.getAuthList().stream()
                .map(auth -> new SimpleGrantedAuthority("ROLE_" + auth.getAuth().name()))
                .toList();

        // 권한 ID 추출
        long authId = user.getAuthList().isEmpty() ? 0L : user.getAuthList().get(0).getId();

        // 우리가 만든 AuthUserDTO 반환 (이전 단계에서 만든 생성자 기준)
        return new AuthUserDTO(
                user.getEmail(),
                user.getPwd(),
                user.getNickName(),
                user.getAuthList().get(0).getAuth().name(),
                authId,
                authorities
        );
    }
}
