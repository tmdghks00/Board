package com.example.communityboard.controller;

import com.example.communityboard.dto.UserDto;
import com.example.communityboard.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    // 비밀번호 변경 페이지
    @GetMapping("/user/change-password")
    public String changePasswordForm() {
        return "user/change-password";
    }

    // 비밀번호 변경 처리
    @PostMapping("/user/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        String errorMessage = userService.changePassword(username, currentPassword, newPassword, confirmPassword);

        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute("passwordError", errorMessage);
            return "redirect:/user/change-password";
        }

        redirectAttributes.addFlashAttribute("passwordSuccess", "비밀번호가 성공적으로 변경되었습니다.");
        return "redirect:/user/change-password";
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

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("username");
        return "redirect:/login";
    }
}
