package com.community.demo.controller;

import com.community.demo.dto.AuthUserDTO;
import com.community.demo.dto.BoardDTO;
import com.community.demo.dto.BoardFileDTO;
import com.community.demo.dto.FileDTO;
import com.community.demo.entity.Board;
import com.community.demo.handler.FileHandler;
import com.community.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board/*")
@Controller
public class BoardController {
    private final BoardService boardService;
    private final FileHandler fileHandler;

    @GetMapping("/register")
    public void register(Model model) {
        // Thymeleaf에서 ${board}를 참조하고 있다면, 빈 객체라도 넣어줘야 에러가 안 납니다.
        model.addAttribute("board", new BoardDTO());
    }

//    @PostMapping("/register")
//    public String register(BoardDTO boardDTO,
//                           @RequestParam(name = "files", required = false) MultipartFile[] files) {
//
//        List<FileDTO> fileList = null;
//        if(files != null && files.length > 0 && files[0].getSize() > 0){
//            fileList = fileHandler.uploadFile(files);
//        }
//
//        // 1. 먼저 변수를 생성 (이 라인이 서비스 호출보다 위에 있어야 합니다!)
//        BoardFileDTO boardFileDTO = new BoardFileDTO(boardDTO, fileList);
//
//        // 2. 생성된 변수를 서비스에 전달
//        Long bno = boardService.insert(boardFileDTO);
//
//        return "redirect:/board/detail?bno=" + bno;
//    }

    @PostMapping("/register")
    public String register(BoardDTO boardDTO,
                           @RequestParam(name = "files", required = false) MultipartFile[] files) {

        // [체크 1] 데이터 유입 확인
        log.info(">>> Register boardDTO: {}", boardDTO);
        log.info(">>> Register files length: {}", (files != null ? files.length : 0));

        List<FileDTO> fileList = null;
        if(files != null && files.length > 0 && files[0].getSize() > 0){
            fileList = fileHandler.uploadFile(files);
            log.info(">>> File upload result: {}", fileList); // 파일 핸들러가 일을 했는지 확인
        }

        BoardFileDTO boardFileDTO = new BoardFileDTO(boardDTO, fileList);
        Long bno = boardService.insert(boardFileDTO);

        log.info(">>> Registered bno: {}", bno); // bno가 생성되었는지 확인

        return "redirect:/"; // 메인으로 리다이렉트
    }

    @GetMapping("/detail")
    public void detail(@RequestParam("bno") long bno, Model model){
        // 1. 게시글 정보 가져오기
        BoardDTO boardDTO = boardService.getDetail(bno);

        // 2. 해당 게시글의 파일 리스트 가져오기 (서비스에 이 로직이 있어야 함)
        List<FileDTO> fileList = boardService.getFileList(bno);
        boardDTO.setFileList(fileList);

        model.addAttribute("board", boardDTO);
        log.info(">>> board with files >> {}", boardDTO);
    }
}
