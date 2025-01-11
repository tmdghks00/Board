package com.example.communityboard.dto;

import com.example.communityboard.domain.entity.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional; // Optional import

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDto {
    private Long id; // 게시글 ID
    private String author; // 작성자
    private String title; // 제목
    private String content; // 내용
    private Long fileId; // Optional<Long>에서 Long으로 변경
    private LocalDateTime createdDate; // 생성일
    private LocalDateTime modifiedDate; // 수정일
    private FileDto fileDto; // 첨부파일 정보
    private int likeCount; // 좋아요 수 필드 추가
    private Integer viewCount;
    private int commentCount;

    public Board toEntity() {  // BoardDto를 Board 엔티티로 변환하는 메서드
        Board build = Board.builder()
                .id(id) // ID 설정
                .author(author) // 작성자 설정
                .title(title) // 제목 설정
                .content(content) // 내용 설정
                .fileId(fileId) // Optional 제거
                .build(); // 엔티티 빌드
        return build; // 변환된 엔티티 반환
    }

    @Builder
    public BoardDto(Long id, String author, String title, String content, Long fileId,
                    LocalDateTime createdDate, LocalDateTime modifiedDate, FileDto fileDto,
                    int likeCount, Integer viewCount, int commentCount) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.fileId = fileId;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.fileDto = fileDto;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }
}