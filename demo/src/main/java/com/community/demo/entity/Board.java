package com.community.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "fileList") // 순환 참조 방지
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="board")
public class Board extends TimeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String writer;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(name = "read_count", columnDefinition = "int default 0")
    private int readCount;

    @Column(name = "cmt_qty", columnDefinition = "int default 0")
    private int cmtQty;

    @Column(name = "file_qty", columnDefinition = "int default 0")
    private int fileQty;

    // File 엔티티와의 일대다 관계 설정
    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<File> fileList = new ArrayList<>();

    // 편의 메서드: 조회수 증가 등 필요 시 추가
    public void addReadCount() {
        this.readCount++;
    }
}