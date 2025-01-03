package com.example.communityboard.service;

import com.example.communityboard.domain.entity.Board;
import com.example.communityboard.domain.repository.BoardRepository;
import com.example.communityboard.dto.BoardDto;
import com.example.communityboard.dto.FileDto;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
public class BoardService {
    private BoardRepository boardRepository;
    private FileService fileService;

    public BoardService(BoardRepository boardRepository, FileService fileService) {
        this.boardRepository = boardRepository;
        this.fileService = fileService;
    }

    @Transactional    // 게시글 저장 메서드: 게시글 DTO를 엔티티로 변환하여 저장
    public Long savePost(BoardDto boardDto) {
        return boardRepository.save(boardDto.toEntity()).getId();
    }

    @Transactional   // 모든 게시글 목록 조회 메서드
    public List<BoardDto> getBoardList() {
        List<Board> boardList = boardRepository.findAll(); // 모든 게시글 조회
        List<BoardDto> boardDtoList = new ArrayList<>();

        for(Board board : boardList) {
            BoardDto boardDto = BoardDto.builder()
                    .id(board.getId()) // ID 설정
                    .author(board.getAuthor()) // 작성자 설정
                    .title(board.getTitle()) // 제목 설정
                    .content(board.getContent()) // 내용 설정
                    .createdDate(board.getCreatedDate()) // 생성일 설정
                    .build();
            boardDtoList.add(boardDto);
        }
        return boardDtoList;
    }

    @Transactional     // 특정 게시글 조회 메서드: ID로 게시글 정보를 가져옴
    public BoardDto getPost(Long id) {
        Board board = boardRepository.findById(id).get(); // ID로 게시글 조회
        FileDto fileDto = null;
        if (board.getFileId() != null) {
            fileDto = fileService.getFile(board.getFileId()); // 첨부파일 정보 가져오기 (있을 경우)
        }

        BoardDto boardDto = BoardDto.builder()
                .id(board.getId())
                .author(board.getAuthor())
                .title(board.getTitle())
                .content(board.getContent())
                .fileId(board.getFileId())
                .createdDate(board.getCreatedDate())
                .fileDto(fileDto)  // 파일 정보 추가
                .build();
        return boardDto;
    }

    @Transactional   // 게시글 삭제 메서드: ID로 게시글 삭제 요청 처리
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }
}