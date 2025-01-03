package com.example.communityboard.domain.repository;

import com.example.communityboard.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdOrderByCreatedDateDesc(Long boardId);
}
