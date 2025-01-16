package com.example.communityboard.service;

import com.example.communityboard.domain.entity.Board;
import com.example.communityboard.domain.entity.Comment;
import com.example.communityboard.domain.repository.BoardRepository;
import com.example.communityboard.domain.repository.CommentRepository;
import com.example.communityboard.dto.CommentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
    }

    // 댓글 저장
    @Transactional
    public CommentDto saveComment(CommentDto commentDto) {
        Board board = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .author(commentDto.getAuthor())
                .board(board)
                .build();

        Comment savedComment = commentRepository.save(comment);
        // 댓글 수 증가
        board.incrementCommentCount();
        boardRepository.save(board);
        return convertEntityToDto(savedComment);
    }

    // 게시글별 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedDateDesc(boardId).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    // Entity를 DTO로 변환
    private CommentDto convertEntityToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .createdDate(comment.getCreatedDate())
                .boardId(comment.getBoard().getId())
                .build();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        Board board = comment.getBoard();

        // 댓글 삭제
        commentRepository.delete(comment);

        // 댓글 수 감소
        board.decrementCommentCount();
        boardRepository.save(board);
    }
}
