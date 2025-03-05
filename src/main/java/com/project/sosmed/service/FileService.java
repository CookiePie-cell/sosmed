package com.project.sosmed.service;

import com.project.sosmed.entity.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    FileMetadata uploadFile(MultipartFile file);

    List<FileMetadata> saveAll(List<FileMetadata> fileMetadataList);

}
