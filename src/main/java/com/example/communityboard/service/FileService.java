package com.example.communityboard.service;


import com.example.communityboard.domain.entity.File;
import com.example.communityboard.domain.repository.FileRepository;
import com.example.communityboard.dto.FileDto;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class FileService {
    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional
    public Long saveFile(FileDto fileDto) {
        return fileRepository.save(fileDto.toEntity()).getId();
    }

    @Transactional // 특정 파일 조회 메서드: ID로 파일 정보를 가져옴
    public FileDto getFile(Long id) {
        File file = fileRepository.findById(id).get();

        FileDto fileDto = FileDto.builder()
                .id(id) // ID 설정
                .origFilename(file.getOrigFilename()) // 원래 파일 이름 설정
                .filename(file.getFilename()) // 해시된 파일 이름 설정
                .filePath(file.getFilePath()) // 파일 경로 설정
                .build();
        return fileDto;
    }
}