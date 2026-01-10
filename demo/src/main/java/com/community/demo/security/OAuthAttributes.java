package com.community.demo.security; // 구조에 맞게 수정

import com.community.demo.entity.AuthRole;
import com.community.demo.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name; // 소셜에서 가져온 이름(닉네임)
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        } else if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build(); // .builder() -> .build()로 수정
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 우리 프로젝트의 User 엔티티 구조에 맞게 변환
    public User toEntity() {
        // 소셜 유저는 비밀번호가 필요 없지만 DB 제약조건이 nullable=false이므로 임의값 생성
        String dummyPassword = PasswordEncoderFactories.createDelegatingPasswordEncoder()
                .encode("social_" + UUID.randomUUID().toString().substring(0, 6));

        User user = User.builder()
                .email(email)
                .pwd(dummyPassword)
                .nickName(name) // 소셜의 name을 우리 엔티티의 nickName으로 매핑
                .build();

        // 권한 추가 (User 엔티티에 정의한 addAuth 메서드 활용)
        user.addAuth(AuthRole.USER);

        return user;
    }
}