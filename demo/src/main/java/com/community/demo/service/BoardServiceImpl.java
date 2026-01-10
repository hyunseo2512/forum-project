package com.community.demo.service;

import com.community.demo.dto.BoardDTO;
import com.community.demo.dto.BoardFileDTO;
import com.community.demo.dto.FileDTO;
import com.community.demo.entity.Board;
import com.community.demo.entity.File;
import com.community.demo.repository.BoardRepository;
import com.community.demo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;

    // 1. 커서 기반 목록 조회 (무한 스크롤용)
    @Override
    @Transactional(readOnly = true)
    public List<BoardDTO> getListWithCursor(Long lastBno, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<Board> result;

        if (lastBno == null || lastBno == 0) {
            result = boardRepository.findFirstPage(pageable);
        } else {
            result = boardRepository.findNextPage(lastBno, pageable);
        }

        // 명확하게 convertBoardToDto 사용 (Board -> BoardDTO)
        return result.stream()
                .map(this::convertBoardToDto)
                .toList();
    }

    // 2. 전체 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<BoardDTO> getList() {
        return boardRepository.findAll().stream()
                .map(this::convertBoardToDto)
                .toList();
    }

    // 3. 상세 조회 (조회수 증가 포함)
    @Override
    @Transactional
    public BoardDTO getDetail(long bno) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 조회수 증가 로직 (엔티티에 정의된 메서드 호출 권장)
        board.setReadCount(board.getReadCount() + 1);

        BoardDTO boardDTO = convertBoardToDto(board);
        // 이미 convertBoardToDto 내부에서 fileList를 세팅하므로 중복 세팅 불필요
        return boardDTO;
    }

    // 4. 일반 게시글 저장
    @Override
    @Transactional
    public Long insert(BoardDTO boardDTO) {
        Board board = convertBoardToEntity(boardDTO);
        return boardRepository.save(board).getBno();
    }

//    // 5. 게시글 + 파일 함께 저장
//    @Override
//    @Transactional
//    public Long insert(BoardFileDTO boardFileDTO) {
//        // 게시글 정보 저장
//        Board board = boardRepository.save(convertBoardToEntity(boardFileDTO.getBoardDTO()));
//        Long bno = board.getBno();
//
//        // 파일 정보 저장
//        if (boardFileDTO.getFileList() != null && !boardFileDTO.getFileList().isEmpty()) {
//            for (FileDTO fileDTO : boardFileDTO.getFileList()) {
//                fileDTO.setBno(bno); // FK 세팅
//                fileRepository.save(convertFileToEntity(fileDTO));
//            }
//        }
//        return bno;
//    }

    @Override
    @Transactional
    public Long insert(BoardFileDTO boardFileDTO) {
        // 1. 게시글 먼저 저장해서 bno를 생성함
        Board board = boardRepository.save(convertBoardToEntity(boardFileDTO.getBoardDTO()));

        // 2. 파일 저장 시 게시글 객체를 연결해줌
        if (boardFileDTO.getFileList() != null) {
            for (FileDTO fileDTO : boardFileDTO.getFileList()) {
                File file = convertFileToEntity(fileDTO);
                file.setBoard(board); // 핵심: 이 코드가 DB의 bno 컬럼을 채워줍니다.
                fileRepository.save(file);
            }
        }
        return board.getBno();
    }

    // 6. 오늘 생성된 파일 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<FileDTO> getTodayFileList(String today) {
        return fileRepository.findBySaveDir(today)
                .map(files -> files.stream()
                        .map(this::convertFileToDto)
                        .toList())
                .orElse(null);
    }

//    // 7. 특정 게시글의 파일 목록 조회
//    @Override
//    @Transactional(readOnly = true)
//    public List<FileDTO> getFileList(long bno) {
//        return fileRepository.findAllByBno(bno).stream()
//                .map(this::convertFileToDto)
//                .toList();
//    }
    // BoardServiceImpl.java 내 관련 메서드 수정
    @Override
    @Transactional(readOnly = true)
    public List<FileDTO> getFileList(long bno) {
        // 바뀐 메서드명 findByBoardBno 호출
        List<File> fileEntities = fileRepository.findByBoardBno(bno);

        return fileEntities.stream()
                .map(this::convertFileToDto)
                .toList();
    }
}