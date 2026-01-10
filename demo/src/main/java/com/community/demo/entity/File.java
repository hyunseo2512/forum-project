package com.community.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "board")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "file")
public class File extends TimeBase {

    @Id
    private String uuid;

    @Column(name = "save_dir")
    private String saveDir;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private long fileSize;

    // [수정 포인트] 기존의 private Long bno; 필드는 삭제하세요.
    // 대신 아래의 연관관계 필드가 DB의 'bno' 컬럼을 관리합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bno")
    private Board board;
}