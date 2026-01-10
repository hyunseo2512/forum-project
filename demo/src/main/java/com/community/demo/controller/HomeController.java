package com.community.demo.controller;

import com.community.demo.dto.BoardDTO;
import com.community.demo.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class HomeController {
    private final BoardService boardService;

    @GetMapping("/")
    public String index(@RequestParam(value = "lastBno", required = false) Long lastBno,
                        HttpServletRequest request, Model model) {

        List<BoardDTO> boardList = boardService.getListWithCursor(lastBno, 5);
        model.addAttribute("boardList", boardList);

        // AJAX(무한스크롤) 요청일 때만 프래그먼트 리턴
        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "index :: boardItems";
        }

        // 브라우저 직접 접속 시에는 전체 페이지 리턴
        return "index";
    }
}

