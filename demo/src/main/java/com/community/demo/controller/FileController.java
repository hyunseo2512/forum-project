package com.community.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@RestController
public class FileController {

    // FileHandler에서 설정한 UP_DIR과 동일해야 합니다.
    private final String uploadPath = "/home/zxcvne/forum-project/_myProject/_java/_fileUpload/";

    @GetMapping("/view")
    public ResponseEntity<Resource> displayFile(String fileName) {
        String fullPath = uploadPath + File.separator + fileName;
        log.info(">>> 찾으려는 파일 실제 경로: {}", fullPath); // 이 로그가 제대로 찍히는지 확인
        // fileName 예: "2026/01/11/uuid_test.jpg"
        Resource resource = new FileSystemResource(uploadPath + fileName);

        if (!resource.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders header = new HttpHeaders();
        try {
            // 파일 확장자에 따라 image/jpeg, image/png 등을 자동으로 설정
            header.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }
}