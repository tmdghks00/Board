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

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column
    private String username;  // 작성자의 실제 로그인 아이디 저장

    @Column(nullable = false)
    private Integer viewCount = 0;  // null이 아닌 0으로 초기화

    @Column(nullable = false)
    private Integer commentCount = 0; // 댓글 수 기본값

    @Builder
    public Board(Long id, String author, String title, String content, String username, Long fileId) {   this.id = id; // 게시글 ID 초기화
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.username = username;
        this.fileId = fileId;
        this.viewCount = 0;

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

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1 : this.viewCount + 1;
    }


    // 댓글 수를 반환하는 메서드 추가
    public int getCommentCount() {
        return comments.size();
    }

    // getter 수정
    public Integer getViewCount() {
        return viewCount != null ? viewCount : 0;
    }

    // setViewCount 메서드 추가
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    // 댓글 수를 증가시키는 메서드
    public void incrementCommentCount() {
        this.commentCount = (this.commentCount == null) ? 1 : this.commentCount + 1;
    }

    // 댓글 수를 감소시키는 메서드
    public void decrementCommentCount() {
        if (this.commentCount != null && this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void setUsername(String username) {  // setUsername() 메서드 추가
        this.username = username;
    }
}
