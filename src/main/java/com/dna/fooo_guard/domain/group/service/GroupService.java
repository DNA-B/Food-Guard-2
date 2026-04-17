package com.dna.fooo_guard.domain.group.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dna.fooo_guard.domain.group.dto.GroupCreateRequest;
import com.dna.fooo_guard.domain.group.dto.GroupEditRequest;
import com.dna.fooo_guard.domain.group.dto.GroupResponse;
import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.group.repository.GroupRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.domain.userGroup.entity.UserGroup;
import com.dna.fooo_guard.domain.userGroup.repository.UserGroupRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    public void createGroup(GroupCreateRequest dto, Long userId) {
        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Group group = groupRepository.save(dto.toEntity(manager));
        group.addMember(manager); // dirty checking
    }

    public GroupResponse findGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
        return GroupResponse.from(group);
    }

    public List<GroupResponse> findAllByUserId(Long userId) {
        List<UserGroup> userGroups = userGroupRepository.findAllByUserId(userId);

        if (userGroups.isEmpty()) {
            throw new CustomException(ErrorCode.GROUP_NOT_FOUND);
        }

        return userGroups.stream()
                .map(userGroup -> GroupResponse.from(userGroup.getGroup()))
                .toList();
    }

    // dirtyCheking으로 DB 자동 반영하기
    public void editGroup(Long groupId, GroupEditRequest dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
        group.edit(dto);
    }

}
