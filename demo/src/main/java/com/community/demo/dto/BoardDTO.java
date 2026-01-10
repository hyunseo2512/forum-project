package com.community.demo.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private Long bno;
    private String title;
    private String writer;
    private String content;
    private int readCount;
    private int cmtQty;
    private int fileQty;

    // 등록 및 수정 시간
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    // 게시글에 포함된 파일/이미지 리스트
    private List<FileDTO> fileList;
}