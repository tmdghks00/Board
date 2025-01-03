package com.example.communityboard.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.example.communityboard.domain.entity.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) /* JPA에게 해당 Entity는 Auditiong 기능을 사용함을 알립니다. */
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 10, nullable = false)
    private String author;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private Long fileId;

    @Column
    private Integer likeCount = 0;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public Board(Long id, String author, String title, String content, Long fileId) {
        this.id = id; // 게시글 ID 초기화
        this.author = author; // 작성자 초기화
        this.title = title; // 제목 초기화
        this.content = content; // 내용 초기화
        this.fileId = fileId; // 첨부파일 ID 초기화
    }

    public void incrementLikeCount() {
        if (this.likeCount == null) {
            this.likeCount = 1;
        } else {
            this.likeCount++;
        }
    }

    @ElementCollection
    @CollectionTable(name = "board_likes", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "user_id")
    private Set<String> likedUsers = new HashSet<>();

    public boolean toggleLike(String userId) {
        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
            if (likeCount > 0) likeCount--;
            return false;
        } else {
            likedUsers.add(userId);
            if (likeCount == null) likeCount = 1;
            else likeCount++;
            return true;
        }
    }

    public int getLikeCount() {
        return likeCount == null ? 0 : likeCount;
    }


}
