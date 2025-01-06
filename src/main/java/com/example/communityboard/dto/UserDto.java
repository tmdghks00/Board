package com.example.communityboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String username;
    private String password;
    private String passwordConfirm;  // 비밀번호 확인 필드 추가
    private String nickname;

    // 닉네임 유효성 검사 추가
    public boolean isValidNickname() {
        return nickname != null &&
                nickname.length() >= 2 &&
                nickname.length() <= 20 &&
                nickname.matches("^[가-힣a-zA-Z0-9]+$");
    }

    // 아이디 유효성 검사
    public boolean isValidUsername() {
        return username != null &&
                username.matches("^[a-zA-Z0-9]{4,12}$");
    }

    // 비밀번호 유효성 검사
    public boolean isValidPassword() {
        return password != null &&
                password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    }

    // 비밀번호 확인 일치 검사
    public boolean isPasswordMatch() {
        return password != null && password.equals(passwordConfirm);
    }
}