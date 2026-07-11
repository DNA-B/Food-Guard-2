package com.dna.fooo_guard.domain.group.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private FoodRepository foodRepository;
    @InjectMocks
    private GroupService groupService;

    @Nested
    @DisplayName("성공 케이스")
    class Success {

        @Test
        @DisplayName("그룹 생성 성공 - 매니저 추가 검증")
        void createGroup_Success() {
            // ------------------ [GIVEN] ------------------
            Long userId = 1L;
            User manager = User.builder().id(userId).build();
            GroupCreateRequest request = GroupCreateRequest.builder()
                    .name("우리집 냉장고")
                    .description("가족 공유 냉장고")
                    .build();

            given(userRepository.getReferenceById(userId)).willReturn(manager);

            // ------------------ [WHEN] ------------------
            groupService.createGroup(request, userId);

            // ------------------ [THEN] ------------------
            verify(userRepository).getReferenceById(userId);

            ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
            verify(groupRepository).save(groupCaptor.capture());
            Group savedGroup = groupCaptor.getValue();

            assertThat(savedGroup.getMembers()).hasSize(1);
            assertThat(savedGroup.getMembers().get(0).getUser()).isEqualTo(manager);
        }

        @Test
        @DisplayName("그룹 ID 조회 성공")
        void findGroupById_Success() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            User manager = User.builder().id(1L).build();
            Group group = Group.builder()
                    .id(groupId)
                    .name("공유 냉장고")
                    .description("테스트")
                    .manager(manager)
                    .build();

            given(groupRepository.findById(groupId)).willReturn(Optional.of(group));

            // ------------------ [WHEN] ------------------
            GroupResponse response = groupService.findGroupById(groupId);

            // ------------------ [THEN] ------------------
            verify(groupRepository).findById(groupId);
            assertThat(response.getId()).isEqualTo(groupId);
            assertThat(response.getName()).isEqualTo("공유 냉장고");
        }

        @Test
        @DisplayName("유저 ID 기준 소속 그룹 전체 조회 성공")
        void findAllByUserId_Success() {
            // ------------------ [GIVEN] ------------------
            Long userId = 1L;
            Group group1 = Group.builder().id(10L).name("그룹1").build();
            Group group2 = Group.builder().id(20L).name("그룹2").build();

            UserGroup userGroup1 = UserGroup.builder().group(group1).build();
            UserGroup userGroup2 = UserGroup.builder().group(group2).build();

            given(userGroupRepository.findAllByUserIdWithGroup(userId)).willReturn(List.of(userGroup1, userGroup2));

            // ------------------ [WHEN] ------------------
            List<GroupResponse> responses = groupService.findAllByUserId(userId);

            // ------------------ [THEN] ------------------
            verify(userGroupRepository).findAllByUserIdWithGroup(userId);
            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting("id", "name")
                    .containsExactly(tuple(10L, "그룹1"), tuple(20L, "그룹2"));
        }

        @Test
        @DisplayName("그룹 내 전체 식품 조회 성공")
        void findAllFoodById_Success() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            Food food1 = Food.builder().id(1L).name("음식1").build();
            Food food2 = Food.builder().id(2L).name("음식2").build();

            given(foodRepository.findAllByGroupId(groupId)).willReturn(List.of(food1, food2));

            // ------------------ [WHEN] ------------------
            List<FoodResponse> responses = groupService.findAllFoodById(groupId);

            // ------------------ [THEN] ------------------
            verify(foodRepository).findAllByGroupId(groupId);
            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting("id", "name")
                    .containsExactly(tuple(1L, "음식1"), tuple(2L, "음식2"));
        }

        @Test
        @DisplayName("그룹 정보 수정 성공")
        void editGroup_Success() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            Long userId = 1L;
            User manager = User.builder().id(userId).build();
            Group originGroup = Group.builder()
                    .id(groupId)
                    .name("원래 이름")
                    .description("원래 설명")
                    .manager(manager)
                    .build();

            GroupEditRequest request = GroupEditRequest.builder()
                    .name("바뀐 이름")
                    .description("바뀐 설명")
                    .build();

            given(groupRepository.findById(groupId)).willReturn(Optional.of(originGroup));

            // ------------------ [WHEN] ------------------
            groupService.editGroup(groupId, userId, request);

            // ------------------ [THEN] ------------------
            verify(groupRepository).findById(groupId);
            assertThat(originGroup.getName()).isEqualTo("바뀐 이름");
            assertThat(originGroup.getDescription()).isEqualTo("바뀐 설명");
        }

        @Test
        @DisplayName("그룹 탈퇴 성공 - 혼자 남은 그룹인 경우 자동 해체 검증")
        void groupExit_Success_LastMember() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            Long userId = 1L;
            User manager = User.builder().id(userId).build();

            Group group = Group.builder()
                    .id(groupId)
                    .manager(manager)
                    .members(new ArrayList<>())
                    .build();
            group.addMember(manager);

            Food food = Food.builder().id(5L).build();

            given(groupRepository.findById(groupId)).willReturn(Optional.of(group));
            given(foodRepository.findAllByUserIdAndGroupId(userId, groupId)).willReturn(List.of(food));

            // ------------------ [WHEN] ------------------
            groupService.groupExit(groupId, userId);

            // ------------------ [THEN] ------------------
            verify(foodRepository).findAllByUserIdAndGroupId(userId, groupId);
            verify(groupRepository).delete(group);
        }

        @Test
        @DisplayName("그룹 탈퇴 성공 - 방장이 탈퇴할 때 차기 방장 위임 검증")
        void groupExit_Success_ManagerExit() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            Long currentManagerId = 1L;
            Long nextManagerId = 2L;

            User currentManager = User.builder().id(currentManagerId).build();
            User nextManager = User.builder().id(nextManagerId).build();

            Group group = Group.builder()
                    .id(groupId)
                    .manager(currentManager)
                    .members(new ArrayList<>())
                    .build();

            group.addMember(currentManager);
            group.addMember(nextManager);

            UserGroup exitMemberMapping = group.getMembers().get(0);

            given(groupRepository.findById(groupId)).willReturn(Optional.of(group));
            given(foodRepository.findAllByUserIdAndGroupId(currentManagerId, groupId)).willReturn(List.of());
            given(userGroupRepository.findByGroupIdAndUserId(groupId, currentManagerId))
                    .willReturn(Optional.of(exitMemberMapping));

            // ------------------ [WHEN] ------------------
            groupService.groupExit(groupId, currentManagerId);

            // ------------------ [THEN] ------------------
            assertThat(group.getManager()).isEqualTo(nextManager);
            assertThat(group.getMembers()).hasSize(1);
            assertThat(group.getMembers().get(0).getUser()).isEqualTo(nextManager);
        }

        @Test
        @DisplayName("그룹 탈퇴 성공 - 일반 멤버 탈퇴")
        void groupExit_Success_RegularMember() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            Long managerId = 1L;
            Long memberId = 2L;

            User manager = User.builder().id(managerId).build();
            User member = User.builder().id(memberId).build();

            Group group = Group.builder()
                    .id(groupId)
                    .manager(manager)
                    .members(new ArrayList<>())
                    .build();

            group.addMember(manager);
            group.addMember(member);

            UserGroup exitMemberMapping = group.getMembers().get(1);

            given(groupRepository.findById(groupId)).willReturn(Optional.of(group));
            given(foodRepository.findAllByUserIdAndGroupId(memberId, groupId)).willReturn(List.of());
            given(userGroupRepository.findByGroupIdAndUserId(groupId, memberId))
                    .willReturn(Optional.of(exitMemberMapping));

            // ------------------ [WHEN] ------------------
            groupService.groupExit(groupId, memberId);

            // ------------------ [THEN] ------------------
            assertThat(group.getManager()).isEqualTo(manager);
            assertThat(group.getMembers()).hasSize(1);
            assertThat(group.getMembers().get(0).getUser()).isEqualTo(manager);
        }

        @Test
        @DisplayName("그룹 삭제 성공")
        void deleteGroup_Success() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            User manager = User.builder().id(1L).build();
            Group group = Group.builder().id(groupId).manager(manager).build();

            given(groupRepository.findById(groupId)).willReturn(Optional.of(group));

            // ------------------ [WHEN] ------------------
            groupService.deleteGroup(groupId, 1L);

            // ------------------ [THEN] ------------------
            verify(groupRepository).findById(groupId);
            verify(groupRepository, times(1)).delete(group);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class Failure {

        @Test
        @DisplayName("그룹 조회 실패 - 존재하지 않는 그룹")
        void findGroupById_Fail_NotFound() {
            // ------------------ [GIVEN] ------------------
            Long wrongId = 999L;
            given(groupRepository.findById(wrongId)).willReturn(Optional.empty());

            // ------------------ [WHEN & THEN] ------------------
            Throwable thrown = catchThrowable(() -> groupService.findGroupById(wrongId));

            verify(groupRepository).findById(wrongId);
            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.GROUP_NOT_FOUND));
        }

        @Test
        @DisplayName("그룹 수정 실패 - 권한 없음(방장이 아님)")
        void editGroup_Fail_AccessDenied() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            Long managerId = 1L;
            Long wrongUserId = 2L;

            User manager = User.builder().id(managerId).build();
            Group group = Group.builder().id(groupId).manager(manager).build();
            GroupEditRequest request = GroupEditRequest.builder().name("수정시도").build();

            given(groupRepository.findById(groupId)).willReturn(Optional.of(group));

            // ------------------ [WHEN & THEN] ------------------
            Throwable thrown = catchThrowable(() -> groupService.editGroup(groupId, wrongUserId, request));

            verify(groupRepository).findById(groupId);
            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(
                            ex -> assertThat(((CustomException) ex).getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED));
        }

        @Test
        @DisplayName("그룹 탈퇴 실패 - 매핑된 그룹 멤버 목록에 존재하지 않음")
        void groupExit_Fail_NotAGroupMember() {
            // ------------------ [GIVEN] ------------------
            Long groupId = 10L;
            Long wrongUserId = 3L;

            User manager = User.builder().id(1L).build();
            User member = User.builder().id(2L).build();

            Group group = Group.builder()
                    .id(groupId)
                    .manager(manager)
                    .members(new ArrayList<>())
                    .build();
            group.addMember(manager);
            group.addMember(member);

            given(groupRepository.findById(groupId)).willReturn(Optional.of(group));
            given(foodRepository.findAllByUserIdAndGroupId(wrongUserId, groupId)).willReturn(List.of());
            given(userGroupRepository.findByGroupIdAndUserId(groupId, wrongUserId)).willReturn(Optional.empty());

            // ------------------ [WHEN & THEN] ------------------
            Throwable thrown = catchThrowable(() -> groupService.groupExit(groupId, wrongUserId));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.NOT_A_GROUP_MEMBER));
        }
    }
}