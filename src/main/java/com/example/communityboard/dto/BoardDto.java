package com.example.communityboard.dto;

import com.example.communityboard.domain.entity.Board;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDto {
    private Long id; // 게시글 ID
    private String author; // 작성자
    private String title; // 제목
    private String content; // 내용
    private Long fileId; // 첨부파일 ID
    private LocalDateTime createdDate; // 생성일
    private LocalDateTime modifiedDate; // 수정일
    private FileDto fileDto; // 첨부파일 정보
    private int likeCount; // 좋아요 수 필드 추가


    public Board toEntity() {  // BoardDto를 Board 엔티티로 변환하는 메서드
        Board build = Board.builder()
                .id(id) // ID 설정
                .author(author) // 작성자 설정
                .title(title) // 제목 설정
                .content(content) // 내용 설정
                .fileId(fileId) // 첨부파일 ID 설정
                .build(); // 엔티티 빌드
        return build; // 변환된 엔티티 반환
    }

    @Builder
    public BoardDto(Long id, String author, String title, String content, Long fileId, LocalDateTime createdDate, LocalDateTime modifiedDate, FileDto fileDto, int likeCount) {        this.id = id; // ID 초기화
        this.author = author; // 작성자 초기화
        this.title = title; // 제목 초기화
        this.content = content; // 내용 초기화
        this.fileId = fileId; // 첨부파일 ID 초기화
        this.createdDate = createdDate; // 생성일 초기화
        this.modifiedDate = modifiedDate; // 수정일 초기화
        this.fileDto = fileDto; // 파일 DTO 초기화
        this.likeCount = likeCount; // 좋아요 개수 초기화

    }
}