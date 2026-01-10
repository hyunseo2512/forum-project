package com.community.demo.service;

import com.community.demo.dto.BoardDTO;
import com.community.demo.dto.BoardFileDTO;
import com.community.demo.dto.FileDTO;
import com.community.demo.entity.Board;
import com.community.demo.entity.File;

import java.util.List;
import java.util.stream.Collectors;

public interface BoardService {

    // [1] Board 변환 메서드
    default Board convertBoardToEntity(BoardDTO boardDTO){
        return Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .writer(boardDTO.getWriter())
                .content(boardDTO.getContent())
                .readCount(boardDTO.getReadCount())
                .cmtQty(boardDTO.getCmtQty())
                .fileQty(boardDTO.getFileQty())
                .build();
    }

    default BoardDTO convertBoardToDto(Board board) {
        return BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .writer(board.getWriter())
                .content(board.getContent())
                .readCount(board.getReadCount())
                .cmtQty(board.getCmtQty())
                .fileQty(board.getFileQty())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .fileList(board.getFileList() != null ?
                        board.getFileList().stream()
                                .map(this::convertFileToDto)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    // [2] File 변환 메서드 (형변환 및 bno 중복 방지 반영)
    default File convertFileToEntity(FileDTO fileDTO){
        return File.builder()
                .uuid(fileDTO.getUuid())
                .saveDir(fileDTO.getSaveDir())
                .fileName(fileDTO.getFileName())
                // int(DTO) -> String(Entity) 변환
                .fileType(String.valueOf(fileDTO.getFileType()))
                .fileSize(fileDTO.getFileSize())
                // bno는 Entity에서 삭제했으므로 여기서 세팅하지 않고 ServiceImpl에서 연관관계로 처리
                .build();
    }

    default FileDTO convertFileToDto(File file){
        return FileDTO.builder()
                .uuid(file.getUuid())
                .saveDir(file.getSaveDir())
                .fileName(file.getFileName())
                // String(Entity) -> int(DTO) 변환 (Null 체크 포함)
                .fileType(file.getFileType() != null ? Integer.parseInt(file.getFileType()) : 0)
                // 연관관계 Board 객체에서 bno 추출
                .bno(file.getBoard() != null ? file.getBoard().getBno() : 0L)
                .fileSize(file.getFileSize())
                .regDate(file.getRegDate())
                .modDate(file.getModDate())
                .build();
    }

    /* 추상 메서드들 */
    Long insert(BoardDTO boardDTO);
    List<BoardDTO> getList();
    BoardDTO getDetail(long bno);
    Long insert(BoardFileDTO boardFileDTO);
    List<FileDTO> getTodayFileList(String today);
    List<FileDTO> getFileList(long bno);
    List<BoardDTO> getListWithCursor(Long lastBno, int size);
}