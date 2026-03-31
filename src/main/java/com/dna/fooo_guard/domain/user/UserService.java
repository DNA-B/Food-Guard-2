package com.dna.fooo_guard.domain.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.dto.UserSignUpRequest;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String signUp(UserSignUpRequest request) {
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .build();
        userRepository.save(user);
        return String.format("유저[%s] - 회원가입", user.getUsername());
    }

    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserResponse.from(user);
    }

    public List<UserResponse> findAllUsersByGroupId(Long groupId) {
        List<User> users = userRepository.findAllByGroupId(groupId);
        return users.stream()
                .map(UserResponse::from)
                .toList();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
