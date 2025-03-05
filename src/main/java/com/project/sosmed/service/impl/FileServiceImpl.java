package com.project.sosmed.service.impl;

import com.project.sosmed.entity.FileMetadata;
import com.project.sosmed.exception.BadRequestException;
import com.project.sosmed.repository.FileMetadataRepository;
import com.project.sosmed.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final String uploadDir;

    private static final String BASE_URL = "http://localhost:8080/api/files/download/";

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif",    // Images
            ".mp3", ".wav",                     // Audio
            ".mp4", ".mkv"                      // Video
    );

    public FileServiceImpl(FileMetadataRepository fileMetadataRepository,
                           @Value("${file.upload-dir}") String uploadDir) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.uploadDir = uploadDir;
    }

    @Override
    public FileMetadata uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Invalid file: file is empty.");
        }

        try {
            Files.createDirectories(Path.of(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory", e);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BadRequestException("Invalid file: filename is missing.");
        }
        validateFileType(originalFilename);
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        Path filePath = Path.of(uploadDir, fileName);
        String mediaType;

        try (InputStream inputStream = file.getInputStream()) {
            // Save file
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // Determine media type
            mediaType = Files.probeContentType(filePath);
            if (mediaType == null) {
                mediaType = URLConnection.guessContentTypeFromStream(new BufferedInputStream(inputStream));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }

        if (mediaType == null) {
            throw new RuntimeException("File type not supported");
        }

        String url = BASE_URL + fileName;

        return FileMetadata.builder()
                .fileName(fileName)
                .fileType(getFileType(originalFilename))
                .mediaType(mediaType)
                .mediaUrl(url)
                .fileSize(file.getSize())
                .build();
    }


    @Override
    public List<FileMetadata> saveAll(List<FileMetadata> fileMetadataList) {
        return fileMetadataRepository.saveAll(fileMetadataList);
    }

    private String getFileType(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }

    private void validateFileType(String originalFilename) {
        String fileType = getFileType(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(fileType)) {
            throw new BadRequestException("File type not supported");
        }

    }

}
