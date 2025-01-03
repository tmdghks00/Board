package com.example.communityboard.dto;

import com.example.communityboard.domain.entity.File;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileDto {
    private Long id; // 파일 ID
    private String origFilename; // 원래 파일 이름
    private String filename; // 해시된 파일 이름 (저장 시 사용)
    private String filePath; // 파일이 저장된 경로

    public File toEntity() {     // FileDto를 File 엔티티로 변환하는 메서드
        File build = File.builder()
                .id(id) // ID 설정
                .origFilename(origFilename) // 원래 파일 이름 설정
                .filename(filename) // 해시된 파일 이름 설정
                .filePath(filePath) // 파일 경로 설정
                .build(); // 엔티티 빌드
        return build; // 변환된 엔티티 반환
    }

    @Builder
    public FileDto(Long id, String origFilename, String filename, String filePath) {
        this.id = id; // ID 초기화
        this.origFilename = origFilename; // 원래 파일 이름 초기화
        this.filename = filename; // 해시된 파일 이름 초기화
        this.filePath = filePath; // 파일 경로 초기화
    }
}