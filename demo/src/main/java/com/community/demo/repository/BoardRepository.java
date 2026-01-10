package com.community.demo.repository;

import com.community.demo.entity.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // JPQL 버전: 엔티티 이름(Board)을 사용하며, 대소문자에 안전합니다.
    // 1. 최초 로딩 및 무한 스크롤 통합 처리를 위해 아래와 같이 작성 가능합니다.

    // 최초 1페이지 (lastBno가 없을 때)
    @Query("SELECT b FROM Board b ORDER BY b.bno DESC")
    List<Board> findFirstPage(Pageable pageable);

    // 2페이지부터 (lastBno 기준)
    @Query("SELECT b FROM Board b WHERE b.bno < :lastBno ORDER BY b.bno DESC")
    List<Board> findNextPage(@Param("lastBno") Long lastBno, Pageable pageable);
}
