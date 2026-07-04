package com.dna.fooo_guard.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.auth.dto.CheckNicknameRequest;
import com.dna.fooo_guard.domain.auth.dto.LoginRequest;
import com.dna.fooo_guard.domain.auth.dto.LoginResponse;
import com.dna.fooo_guard.domain.auth.dto.SignUpRequest;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;
import com.dna.fooo_guard.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public boolean isNicknameAvailable(CheckNicknameRequest dto) {
        return !userRepository.existsByNickname(dto.getNickname());
    }

    @Transactional
    public String signUp(SignUpRequest dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .build();
        userRepository.save(user);
        return String.format("유저[%s] - 회원가입", user.getUsername());
    }

    public LoginResponse login(LoginRequest dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        String token = jwtTokenProvider.createToken(user.getId());
        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
