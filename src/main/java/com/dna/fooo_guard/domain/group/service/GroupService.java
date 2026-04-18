package com.dna.fooo_guard.domain.group.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final FoodRepository foodRepository;

    public void createGroup(GroupCreateRequest dto, Long userId) {
        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Group group = groupRepository.save(dto.toEntity(manager));
        group.addMember(manager); // dirty checking
    }

    @Transactional(readOnly = true)
    public GroupResponse findGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
        return GroupResponse.from(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> findAllByUserId(Long userId) {
        List<UserGroup> userGroups = userGroupRepository.findAllByUserId(userId);
        return userGroups.stream()
                .map(userGroup -> GroupResponse.from(userGroup.getGroup()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> findAllFoodById(Long id) {
        List<Food> foods = foodRepository.findAllByGroupId(id);
        return foods.stream()
                .map(food -> FoodResponse.from(food))
                .toList();
    }

    // dirtyCheking으로 DB 자동 반영하기
    public void editGroup(Long groupId, GroupEditRequest dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
        group.edit(dto);
    }

}
