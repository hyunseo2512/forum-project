package com.community.demo.repository;

import com.community.demo.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String> {

    Optional<List<File>> findBySaveDir(String today);

    // [수정 전] List<File> findAllByBno(long bno);
    // [수정 후] File 엔티티 안의 board 객체의 bno를 참조한다는 뜻입니다.
    List<File> findByBoardBno(long bno);
}