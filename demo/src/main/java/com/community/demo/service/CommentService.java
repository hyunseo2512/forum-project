package com.community.demo.service;

import com.community.demo.dto.CommentDTO;
import com.community.demo.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    default Comment convertDtoToEntity(CommentDTO commentDTO) {
        return Comment.builder()
                .cno(commentDTO.getCno())
                .bno(commentDTO.getBno())
                .writer(commentDTO.getWriter())
                .content(commentDTO.getContent())
                .build();
    }

    // Entity => DTO
    default CommentDTO convertEntityToDto(Comment comment) {
        return  CommentDTO.builder()
                .cno(comment.getCno())
                .bno(comment.getBno())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .regDate(comment.getRegDate())
                .modDate(comment.getModDate())
                .build();
    }


    long post(CommentDTO commentDTO);

    List<CommentDTO> getList(Long bno);

    int modify(CommentDTO commentDTO);

    int remove(Long cno);
}
