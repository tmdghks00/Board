package com.example.communityboard.controller;

// 필요한 클래스 임포트
import com.example.communityboard.domain.entity.User;
import com.example.communityboard.dto.BoardDto;
import com.example.communityboard.dto.CommentDto;
import com.example.communityboard.dto.FileDto;
import com.example.communityboard.service.BoardService;
import com.example.communityboard.service.CommentService;
import com.example.communityboard.service.FileService;
import com.example.communityboard.service.UserService;
import com.example.communityboard.util.MD5Generator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// 이 클래스는 게시판의 요청을 처리하는 컨트롤러입니다.
@Controller
public class BoardController {
    // 서비스 클래스의 인스턴스를 주입받기 위한 필드
    private final BoardService boardService; // 게시판 관련 서비스
    private final FileService fileService;   // 파일 관련 서비스
    private final CommentService commentService;
    private final UserService userService; // 추가


    // 생성자 주입을 통해 서비스 인스턴스를 초기화
    public BoardController(BoardService boardService, FileService fileService, CommentService commentService, UserService userService) {
        this.boardService = boardService;
        this.fileService = fileService;
        this.commentService = commentService;
        this.userService = userService;
    }

    // 게시판 목록 조회 (페이징 적용)
    @GetMapping({"", "/", "/boards"})
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<BoardDto> boardDtoPage = boardService.getBoardList(pageable);
        model.addAttribute("postList", boardDtoPage);
        return "board/list.html";
    }

    // 게시글 작성 페이지로 이동
    @GetMapping("/post")
    public String post() {
        return "board/post.html"; // 게시글 작성 뷰 반환
    }

    // 게시글 작성 처리: 파일 업로드와 게시글 정보를 함께 처리
    @PostMapping("/post")
    public String write(@RequestParam("file") MultipartFile files, BoardDto boardDto, HttpSession session) {
        String username = (String) session.getAttribute("username");
        User user = userService.findByUsername(username);
        boardDto.setAuthor(user.getNickname());  // 작성자를 현재 로그인한 사용자의 닉네임으로 설정

        try {
            String origFilename = files.getOriginalFilename(); // 원래 파일 이름 가져오기
            String filename = new MD5Generator(origFilename).toString(); // MD5 해시로 파일 이름 생성
            String savePath = System.getProperty("user.dir") + "/files"; // 파일 저장 경로 설정

            // 파일 저장 경로 폴더 생성
            File saveFolder = new File(savePath);
            if (!saveFolder.exists()) {
                saveFolder.mkdirs(); // 폴더가 없으면 생성
            }

            String filePath = savePath + "/" + filename; // 전체 파일 경로 설정
            files.transferTo(new File(filePath)); // 파일 저장

            FileDto fileDto = new FileDto(); // 파일 DTO 생성
            fileDto.setOrigFilename(origFilename); // 원래 파일 이름 설정
            fileDto.setFilename(filename); // 해시된 파일 이름 설정
            fileDto.setFilePath(filePath); // 파일 경로 설정

            Long fileId = fileService.saveFile(fileDto); // 파일 정보 저장 후 ID 반환
            boardDto.setFileId(fileId); // 게시글 DTO에 파일 ID 설정
            boardService.savePost(boardDto); // 게시글 저장
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
        }
        return "redirect:/"; // 작성 후 루트 URL로 리다이렉트
    }

    // 게시글 상세 조회 (댓글 목록 포함)
    @GetMapping("/post/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpSession session) {
        BoardDto boardDto = boardService.getPost(id);
        List<CommentDto> comments = commentService.getCommentsByBoardId(id);
        String username = (String) session.getAttribute("username");
        User user = userService.findByUsername(username);
        model.addAttribute("post", boardDto);
        model.addAttribute("file", boardDto.getFileDto());
        model.addAttribute("comments", comments);
        model.addAttribute("currentUserNickname", user.getNickname());
        return "board/detail.html";
    }
    // 댓글 작성
    @PostMapping("/post/{id}/comment")
    public String addComment(@PathVariable("id") Long boardId, @ModelAttribute CommentDto commentDto) {
        commentDto.setBoardId(boardId);
        commentService.saveComment(commentDto);
        return "redirect:/post/" + boardId;
    }

    // 게시글 수정 페이지로 이동: 특정 ID의 게시글을 수정하기 위한 페이지 제공
    @GetMapping("/post/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        BoardDto boardDto = boardService.getPost(id); // ID로 게시글 정보 가져오기
        model.addAttribute("post", boardDto); // 모델에 게시글 정보 추가
        return "board/edit.html"; // 수정 뷰 반환
    }

    // 게시글 수정 처리: 수정된 정보를 저장함
    @PutMapping("/post/edit/{id}")
    public String update(BoardDto boardDto) {
        boardService.savePost(boardDto); // 수정된 게시글 정보 저장
        return "redirect:/"; // 수정 후 루트 URL로 리다이렉트
    }

    // 게시글 삭제 처리: 특정 ID의 게시글 삭제함
    @DeleteMapping("/post/{id}")
    public String delete(@PathVariable("id") Long id) {
        boardService.deletePost(id); // ID로 게시글 삭제 요청
        return "redirect:/"; // 삭제 후 루트 URL로 리다이렉트
    }

    // 파일 다운로드: 특정 ID의 파일 다운로드를 처리함
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {
        FileDto fileDto = fileService.getFile(fileId);  // 첨부파일 정보 가져오기
        Path path = Paths.get(fileDto.getFilePath());  // 파일 경로 설정

        Resource resource;  // Resource 객체 선언
        try {
            resource = new UrlResource(path.toUri());  // URL 리소스 생성
            if (!resource.exists() || !resource.isReadable()) {  // 리소스 존재 여부 확인
                throw new IOException("Could not read file: " + fileDto.getFilePath());
            }
        } catch (MalformedURLException e) {  // 잘못된 URL 예외 처리
            throw new IOException("File not found: " + fileDto.getFilePath());
        }

        return ResponseEntity.ok()  // HTTP 응답 객체 생성
                .contentType(MediaType.APPLICATION_OCTET_STREAM)  // 응답 콘텐츠 타입 설정
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getOrigFilename() + "\"")  // 다운로드 시 원래 파일 이름으로 설정
                .body(resource);  // 응답 본문에 리소스 추가
    }

    @PostMapping("/post/{id}/like") // 좋아요 기능
    @ResponseBody
    public ResponseEntity<Integer> likePost(@PathVariable("id") Long id, HttpSession session) {
        String userId = session.getId(); // 세션 ID를 사용자 ID로 사용
        boolean liked = boardService.toggleLike(id, userId);
        BoardDto boardDto = boardService.getPost(id);
        return ResponseEntity.ok(boardDto.getLikeCount());
    }


}
