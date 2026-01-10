package com.community.demo.security;

import com.community.demo.security.OAuthAttributes;
import com.community.demo.entity.User;
import com.community.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 서비스로부터 OAuth2User 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 현재 진행 중인 서비스 구분 (google, naver, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. OAuth2 로그인 진행 시 키가 되는 필드값 (PK와 같은 의미)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 소셜 서비스별로 유저 정보를 담을 규격화된 DTO (OAuthAttributes)
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 5. 유저 정보 저장 또는 업데이트
        User user = saveOrUpdate(attributes);

        // 6. 우리가 만든 통합 객체(CustomAuthUser) 반환
        // 이제 폼 로그인 유저와 소셜 유저 모두 CustomAuthUser 타입을 가짐
        return new CustomAuthUser(user, attributes.getAttributes());
    }

    /**
     * 유저 정보가 DB에 있으면 업데이트(이름 변경 등), 없으면 새로 저장(회원가입)
     */
    private User saveOrUpdate(OAuthAttributes attributes) {
        // 이메일이 @Id이므로 findById()를 사용하여 조회
        User user = userRepository.findById(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName())) // 기존 회원 정보 갱신
                .orElseGet(() -> attributes.toEntity());          // 신규 회원 가입

        return userRepository.save(user);
    }
}