package com.dna.fooo_guard.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsersByGroupId(Long groupId) {
        List<User> users = userRepository.findAllByGroupId(groupId);

        if (users.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return users.stream()
                .map(UserResponse::from)
                .toList();
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
}
