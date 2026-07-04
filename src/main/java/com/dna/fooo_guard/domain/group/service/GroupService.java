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
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final FoodRepository foodRepository;

    // Helper Functions
    private Group getGroupWithManagerCheck(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

        if (!group.getManager().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return group;
    }
    // Helper end

    @Transactional
    public void createGroup(GroupCreateRequest dto, Long userId) {
        User manager = userRepository.getReferenceById(userId);
        Group group = dto.toEntity(manager);
        group.addMember(manager);
        groupRepository.save(group);
    }

    // TODO: id 이름 컨벤션 통일
    public GroupResponse findGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
        return GroupResponse.from(group);
    }

    // TODO: QueryDSL 속도 비교
    public List<GroupResponse> findAllByUserId(Long userId) {
        // List<UserGroup> userGroups = userGroupRepository.findAllByUserId(userId);
        List<UserGroup> userGroups = userGroupRepository.findAllByUserIdWithGroup(userId);

        return userGroups.stream()
                .map(userGroup -> GroupResponse.from(userGroup.getGroup()))
                .toList();
    }

    public List<FoodResponse> findAllFoodById(Long id) {
        List<Food> foods = foodRepository.findAllByGroupId(id);
        return foods.stream()
                .map(FoodResponse::from)
                .toList();
    }

    // dirtyCheking으로 DB 자동 반영하기
    @Transactional
    public void editGroup(Long groupId, Long userId, GroupEditRequest dto) {
        Group group = getGroupWithManagerCheck(groupId, userId);
        group.edit(dto);
    }

    @Transactional
    public void groupExit(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

        // TODO: QueryDSL
        List<Food> userFoodsInGroup = foodRepository.findAllByUserIdAndGroupId(userId, groupId);
        for (Food food : userFoodsInGroup) {
            food.clearGroup();
        }

        // 멤버가 1명뿐이라면 그룹 삭제
        if (group.getMembers().size() == 1) {
            this.deleteGroup(groupId, userId);
            return;
        }

        // 방장 위임 및 멤버 삭제
        UserGroup exitMember = userGroupRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_A_GROUP_MEMBER));

        if (group.getManager().getId().equals(userId)) {
            User nextManager = group.getMembers().stream()
                    .map(UserGroup::getUser)
                    .filter(user -> !user.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.NO_REMAINING_MEMBER));

            group.changeManager(nextManager);
        }

        group.getMembers().remove(exitMember);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        Group group = getGroupWithManagerCheck(groupId, userId);
        groupRepository.delete(group);
    }
}
