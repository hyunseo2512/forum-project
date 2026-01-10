package com.community.demo.controller;

import com.community.demo.dto.CommentDTO;
import com.community.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/comment/*")
public class CommentController {
    private final CommentService commentService;

    @PostMapping(value="/post",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> post(@RequestBody CommentDTO commentDTO){
        long cno = commentService.post(commentDTO);
        return cno > 0 ? new ResponseEntity<>("1", HttpStatus.OK)
                : new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 경로에서 마지막 / 를 제거하여 매칭 확률을 높입니다.
    @GetMapping(value = "/list/{bno}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentDTO>> list(@PathVariable("bno") Long bno){
        log.info(">>> 댓글 리스트 요청 bno: {}", bno);
        List<CommentDTO> list = commentService.getList(bno);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/modify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> modify(@RequestBody CommentDTO commentDTO) {
        int isOk = commentService.modify(commentDTO);
        return isOk > 0 ? new ResponseEntity<>("1", HttpStatus.OK) : new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping(value = "/remove/{cno}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> remove(@PathVariable("cno") Long cno) {
        int isOk = commentService.remove(cno);
        return isOk > 0 ? new ResponseEntity<>("1", HttpStatus.OK) : new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
