package com.example.communityboard.controller;

import com.example.communityboard.dto.UserDto;
import com.example.communityboard.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원정보 수정 페이지
    @GetMapping("/user/edit")
    public String editForm(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        UserDto userDto = userService.getUserInfo(username);
        model.addAttribute("user", userDto);
        return "user/edit";
    }

    // 회원정보 수정 처리
    @PostMapping("/user/edit")
    public String edit(@ModelAttribute UserDto userDto, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        String errorMessage = userService.updateUserInfo(username, userDto);

        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute("editError", errorMessage);
            return "redirect:/user/edit";
        }

        redirectAttributes.addFlashAttribute("editSuccess", "회원정보가 수정되었습니다.");
        return "redirect:/boards";
    }

    // 회원 탈퇴 처리
    @PostMapping("/user/delete")
    public String delete(HttpSession session) {
        String username = (String) session.getAttribute("username");
        userService.deleteUser(username);
        session.removeAttribute("username");
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(UserDto userDto, HttpSession session, RedirectAttributes redirectAttributes) {
        if (userService.login(userDto)) {
            session.setAttribute("username", userDto.getUsername());
            return "redirect:/boards";
        }
        redirectAttributes.addFlashAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(UserDto userDto, RedirectAttributes redirectAttributes) {
        String errorMessage = userService.register(userDto);
        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute("registerError", errorMessage);
            return "redirect:/register";
        }
        redirectAttributes.addFlashAttribute("registerSuccess", "회원가입이 완료되었습니다.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("username");
        return "redirect:/login";
    }
}