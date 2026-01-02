package com.community.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File extends TimeBase{
    @Id
    private String uuid;
    @Column(name = "save_dir", nullable = false)
    private String saveDir;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_type", nullable = false, columnDefinition = "int default 0")
    private int fileType;
    private long bno;
    @Column(name = "file_size")
    private long fileSize;
}
