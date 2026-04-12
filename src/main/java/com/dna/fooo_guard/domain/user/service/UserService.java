package com.dna.fooo_guard.domain.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.domain.userGroup.dto.UserGroupResponse;
import com.dna.fooo_guard.domain.userGroup.entity.UserGroup;
import com.dna.fooo_guard.domain.userGroup.repository.UserGroupRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    // TODO: N+1 문제
    public List<UserGroupResponse> findUserGroupsById(Long userId) {
        List<UserGroup> userGroups = userGroupRepository.findAllByUserId(userId);

        if (userGroups.isEmpty()) {
            throw new CustomException(ErrorCode.USER_GROUP_NOT_FOUND);
        }

        return userGroups.stream()
                .map(userGroup -> UserGroupResponse.from(userGroup.getGroup()))
                .toList();
    }
}
