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

        // 아이디 중복 검사
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return "이미 사용중인 아이디입니다.";
        }

        // 모든 검증을 통과하면 저장
        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .build();
        userRepository.save(user);
        return null; // 성공 시 null 반환
    }

    @Transactional(readOnly = true)
    public boolean login(UserDto userDto) {
        return userRepository.findByUsername(userDto.getUsername())
                .map(user -> user.getPassword().equals(userDto.getPassword()))
                .orElse(false);
    }
}