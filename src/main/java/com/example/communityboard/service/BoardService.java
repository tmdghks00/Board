package com.example.communityboard.service;

import com.example.communityboard.domain.entity.Board;
import com.example.communityboard.domain.repository.BoardRepository;
import com.example.communityboard.dto.BoardDto;
import com.example.communityboard.dto.FileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileService fileService;

    public BoardService(BoardRepository boardRepository, FileService fileService) {
        this.boardRepository = boardRepository;
        this.fileService = fileService;
    }

    @Transactional(readOnly = true)
    public Page<BoardDto> getBoardList(Pageable pageable, String sortBy) {
        Page<Board> boardPage;
        if (sortBy != null) {
            switch (sortBy) {
                case "latest":
                    boardPage = boardRepository.findAllByOrderByCreatedDateDesc(pageable);
                    break;
                case "comments":
                    boardPage = boardRepository.findAllByOrderByCommentCountDesc(pageable);
                    break;
                case "views":
                    boardPage = boardRepository.findAllByOrderByViewCountDesc(pageable);
                    break;
                default:
                    boardPage = boardRepository.findAll(pageable);
            }
        } else {
            boardPage = boardRepository.findAll(pageable);
        }
        return boardPage.map(this::convertEntityToDto);
    }

    private BoardDto convertEntityToDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .author(board.getAuthor())
                .title(board.getTitle())
                .content(board.getContent())
                .createdDate(board.getCreatedDate())
                .likeCount(board.getLikeCount())
                .viewCount(board.getViewCount())
                .commentCount(board.getCommentCount())
                .fileId(board.getFileId())
                .build();
    }

    @Transactional
    public Long savePost(BoardDto boardDto) {
        return boardRepository.save(boardDto.toEntity()).getId();
    }

    @Transactional(readOnly = true)
    public BoardDto getPost(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        FileDto fileDto = null;
        if (board.getFileId() != null) {
            fileDto = fileService.getFile(board.getFileId());
        }

        return BoardDto.builder()
                .id(board.getId())
                .author(board.getAuthor())
                .title(board.getTitle())
                .content(board.getContent())
                .fileId(board.getFileId())
                .createdDate(board.getCreatedDate())
                .likeCount(board.getLikeCount())
                .viewCount(board.getViewCount())
                .commentCount(board.getCommentCount())
                .fileDto(fileDto)
                .build();
    }

    @Transactional
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public boolean toggleLike(Long id, String userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        boolean liked = board.toggleLike(userId);
        boardRepository.save(board);
        return liked;
    }

    @Transactional
    public void incrementViewCount(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        board.incrementViewCount();
        boardRepository.save(board);
    }
    @Transactional(readOnly = true)
    public Page<BoardDto> searchBoards(String keyword, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return boardPage.map(this::convertEntityToDto);
    }

    @Transactional(readOnly = true)
    public Page<BoardDto> getBoardsSortedByCommentCount(Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAllSortedByCommentCount(pageable);
        return boardPage.map(this::convertEntityToDto);
    }


    public Page<BoardDto> getBoardsSortedByViewCount(Pageable pageable) {
        return boardRepository.findAllByOrderByViewCountDesc(pageable)
                .map(this::convertEntityToDto);
    }

    @Transactional(readOnly = true)
    public Page<BoardDto> searchBoardsByTitle(String titleKeyword, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findByTitleContaining(titleKeyword, pageable);
        return boardPage.map(this::convertEntityToDto);
    }


}
