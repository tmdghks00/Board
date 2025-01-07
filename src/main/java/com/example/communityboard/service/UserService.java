package com.example.communityboard.service;

import com.example.communityboard.domain.entity.User;
import com.example.communityboard.domain.repository.UserRepository;
import com.example.communityboard.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public String register(UserDto userDto) {
        // 유효성 검사
        if (!userDto.isValidUsername()) {
            return "아이디는 4~12글자의 영문자와 숫자만 가능합니다.";
        }
        if (!userDto.isValidPassword()) {
            return "비밀번호는 8글자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.";
        }
        if (!userDto.isPasswordMatch()) {
            return "비밀번호가 일치하지 않습니다.";
        }
        if (!userDto.isValidNickname()) {
            return "닉네임은 2~20자의 한글, 영문, 숫자만 가능합니다.";
        }
        // 아이디 중복 검사
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return "이미 사용중인 아이디입니다.";
        }

        // 모든 검증을 통과하면 저장
        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .nickname(userDto.getNickname())
                .build();
        userRepository.save(user);
        return null;
    }

    @Transactional(readOnly = true)
    public boolean login(UserDto userDto) {
        return userRepository.findByUsername(userDto.getUsername())
                .map(user -> user.getPassword().equals(userDto.getPassword()))
                .orElse(false);
    }

    // 회원정보 조회
    @Transactional(readOnly = true)
    public UserDto getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserDto.builder()
                .username(username)
                .nickname(user.getNickname())
                .build();
    }

    // 회원정보 수정
    @Transactional
    public String updateUserInfo(String username, UserDto userDto) {
        if (!userDto.isValidNickname()) {
            return "닉네임은 2~20자의 한글, 영문, 숫자만 가능합니다.";
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateNickname(userDto.getNickname());
        return null;
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public String changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getPassword().equals(currentPassword)) {
            return "현재 비밀번호가 일치하지 않습니다.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.";
        }

        if (!isValidPassword(newPassword)) {
            return "새 비밀번호는 8글자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.";
        }

        user.updatePassword(newPassword);
        return null;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) { // 최소 8자 이상
            return false;
        }
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    }


}