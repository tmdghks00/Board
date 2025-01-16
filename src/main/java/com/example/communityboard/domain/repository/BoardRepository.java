package com.example.communityboard.domain.repository;

import com.example.communityboard.domain.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByOrderByCreatedDateDesc(Pageable pageable);
    Page<Board> findAllByOrderByCommentCountDesc(Pageable pageable);
    Page<Board> findAllByOrderByViewCountDesc(Pageable pageable);
    Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    Page<Board> findByTitleContaining(String title, Pageable pageable);
    @Query("SELECT b FROM Board b LEFT JOIN b.comments c GROUP BY b.id ORDER BY COUNT(c) DESC")
    Page<Board> findAllSortedByCommentCount(Pageable pageable);

}
