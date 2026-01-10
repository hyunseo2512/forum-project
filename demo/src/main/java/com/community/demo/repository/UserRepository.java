package com.community.demo.repository;

import com.community.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 1. 로그인 전용: 이메일로 검색 시 권한 리스트(authList)를 즉시 로딩(Fetch Join)
     * JPQL을 사용하여 명시적으로 Fetch Join을 수행합니다.
     */
    @Query("select u from User u left join fetch u.authList where u.email = :email")
    Optional<User> findByEmailWithAuth(@Param("email") String email);

    /**
     * 2. 관리자 페이지용: 모든 유저 목록을 가져올 때 권한 리스트도 함께 로딩
     * Distinct를 사용하여 1:N 조인 시 발생하는 중복 데이터 행을 제거합니다.
     */
    @Query("select distinct u from User u left join fetch u.authList")
    List<User> findAllWithAuthList();

    /**
     * 3. 기본 ID 검색 오버라이드: findById 사용 시에도 권한 리스트를 함께 로딩
     * @Id가 email이므로 Spring Data JPA의 기본 메서드인 findById를 활용합니다.
     * @EntityGraph는 JPQL 없이도 특정 연관관계를 즉시 로딩하게 해줍니다.
     */
    @Override
    @EntityGraph(attributePaths = {"authList"})
    Optional<User> findById(String email);

}